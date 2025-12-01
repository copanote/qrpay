package com.bccard.qrpay.auth.service;

import com.bccard.qrpay.auth.domain.JwtToken;
import com.bccard.qrpay.auth.domain.RefreshToken;
import com.bccard.qrpay.auth.repository.RefreshTokenQueryRepository;
import com.bccard.qrpay.auth.repository.RefreshTokenRepository;
import com.bccard.qrpay.auth.security.JwtProvider;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.code.AuthErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenQueryRepository refreshTokenQueryRepository;

    @Transactional
    public JwtToken login(String userId, String password) {

        Member member;
        try {
            member = memberService.findMyLoginId(userId);
        } catch (MemberException e) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIAL);
        }

        // Todo:: auth policy
        if (member.getPasswordErrorCount() > 3) {
            throw new AuthException(AuthErrorCode.ACCOUNT_LOCKED_POLICY);
        }

        String hashed = memberService.hashPassword(password);
        if (!member.getHashedPassword().equals(hashed)) {
            member.onPasswordFail();
            //            memberService.passwordFail(member.getMemberId());
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIAL);
        }

        String at =
                jwtProvider.generateToken(member.getMemberId(), member.getRole().toString());
        String rt = jwtProvider.generateRefreshToken(member.getMemberId());

        Instant now = Instant.now();
        RefreshToken newRefreshToken = RefreshToken.createNew()
                .memberId(member.getMemberId())
                .tokenHash(refreshTokenService.hashRefreshToken(rt))
                .issuedAt(now.toEpochMilli())
                .expiresAt(now.plusMillis(jwtProvider.getJwtProperties().getRefreshTokenExpiration())
                        .toEpochMilli())
                .deviceId("")
                .build();

        RefreshToken saved = refreshTokenRepository.save(newRefreshToken);

        return JwtToken.builder().accessToken(at).refreshToken(rt).build();
    }

    public JwtToken createJwt(String memberId, String role) {
        String at = jwtProvider.generateToken(memberId, role);
        String rt = jwtProvider.generateRefreshToken(memberId);

        Instant now = Instant.now();
        RefreshToken newRefreshToken = RefreshToken.createNew()
                .memberId(memberId)
                .tokenHash(refreshTokenService.hashRefreshToken(rt))
                .issuedAt(now.toEpochMilli())
                .expiresAt(now.plusMillis(jwtProvider.getJwtProperties().getRefreshTokenExpiration())
                        .toEpochMilli())
                .deviceId("")
                .build();

        RefreshToken saved = refreshTokenRepository.save(newRefreshToken);

        return JwtToken.builder().accessToken(at).refreshToken(rt).build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken, "USER_LOGOUT");
        // TODO accesstoken balcklist
    }

    public void revoke(String refreshToken) {
        refreshTokenService.revoke(refreshToken, "ADMIN_REVOKE");
        // TODO accesstoken balcklist
    }

    public JwtToken refresh(String refreshToken) {
        Jws<Claims> claimsJws;
        try {
            claimsJws = jwtProvider.validateAndParse(refreshToken);
        } catch (Exception e) {
            throw new AuthException(e, AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken rt = refreshTokenQueryRepository
                .findByTokenHash(refreshTokenService.hashRefreshToken(refreshToken))
                .orElseThrow(() -> new AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN));

        if (!rt.isValid()) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String subject = claimsJws.getPayload().getSubject();

        Member member;
        try {
            member = memberService.findByMemberId(subject);
        } catch (MemberException e) {
            throw new AuthException(e, AuthErrorCode.INVALID_CREDENTIAL);
        }

        String at =
                jwtProvider.generateToken(member.getMemberId(), member.getRole().toString());
        rt.refresh();

        return JwtToken.builder()
                .accessToken(at)
                //                .refreshToken(refreshToken)
                .build();
    }
}
