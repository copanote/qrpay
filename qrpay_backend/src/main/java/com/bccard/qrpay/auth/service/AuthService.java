package com.bccard.qrpay.auth.service;

import com.bccard.qrpay.auth.domain.JwtToken;
import com.bccard.qrpay.auth.domain.RefreshToken;
import com.bccard.qrpay.auth.repository.RefreshTokenQueryRepository;
import com.bccard.qrpay.auth.repository.RefreshTokenRepository;
import com.bccard.qrpay.auth.security.JwtProvider;
import com.bccard.qrpay.auth.service.dto.ResponseAuthDto;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {


    private final JwtProvider jwtProvider;
    private final MemberService memberService;
    private final MerchantService merchantService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenQueryRepository refreshTokenQueryRepository;

    public Member login(String userId, String password) {
        return memberService.authenticate(userId, password);
    }

    public JwtToken createJwt(String memberId, String role) {
        String at = jwtProvider.generateToken(memberId, role);
        String rt = jwtProvider.generateRefreshToken(memberId);

        Instant now = Instant.now();

        RefreshToken newRefreshToken = RefreshToken.createNew()
                .memberId(memberId)
                .tokenHash(refreshTokenService.hashRefreshToken(rt))
                .issuedAt(now.toEpochMilli())
                .expiresAt(now.plusMillis(jwtProvider.getJwtProperties().getRefreshTokenExpiration()).toEpochMilli())
                .deviceId("")
                .build();

        RefreshToken saved = refreshTokenRepository.save(newRefreshToken);

        Long accessTokenExpiresIn = now.plusMillis(jwtProvider.getJwtProperties().getAccessTokenExpiration()).toEpochMilli();

        return JwtToken.builder()
                .accessToken(at)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .refreshToken(rt)
                .build();
    }

    public void logout(String refreshToken) {
        if (jwtProvider.validateToken(refreshToken)) {
            String memberId = jwtProvider.validateAndGetSubject(refreshToken);
            refreshTokenService.revokeAll(memberId, "USER_LOGOUT");
        } else {
            refreshTokenService.revoke(refreshToken, "USER_LOGOUT");
        }
    }

    public void revoke(String refreshToken) {
        refreshTokenService.revoke(refreshToken, "ADMIN_REVOKE");
        //TODO accesstoken balcklist
    }

    public int revokeAllByMerchant(String merchantId, String reason) {

        Merchant merchant = merchantService.findById(merchantId)
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        List<Member> members = memberService.findMembers(merchant);
        int cnt = 0;
        for (Member member : members) {
            int r = refreshTokenService.revokeAll(member.getMemberId(), reason);
            cnt += r;
        }

        return cnt;
    }


    public JwtToken refresh(String refreshToken) {
        Jws<Claims> claimsJws;
        try {
            claimsJws = jwtProvider.validateAndParse(refreshToken);
        } catch (Exception e) {
            throw new AuthException(e, QrpayErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken rt = refreshTokenQueryRepository
                .findByTokenHash(refreshTokenService.hashRefreshToken(refreshToken))
                .orElseThrow(() -> new AuthException(QrpayErrorCode.NOT_FOUND_REFRESH_TOKEN));

        if (!rt.isValid()) {
            throw new AuthException(QrpayErrorCode.INVALID_REFRESH_TOKEN);
        }

        String subject = claimsJws.getPayload().getSubject();

        Member member;
        try {
            member = memberService.findByMemberId(subject);
        } catch (MemberException e) {
            throw new AuthException(e, QrpayErrorCode.INVALID_CREDENTIAL);
        }

        String at = jwtProvider.generateToken(member.getMemberId(), member.getRole().toString());
        rt.refresh();

        Instant now = Instant.now();
        Long accessTokenExpiresIn = now.plusMillis(jwtProvider.getJwtProperties().getAccessTokenExpiration()).toEpochMilli();


        return JwtToken.builder()
                .accessToken(at)
                .accessTokenExpiresIn(accessTokenExpiresIn)
//                .refreshToken(refreshToken)
                .build();
    }

    //
    public ResponseAuthDto authenticate(String accessToken, String refreshToken) {
        String aToken = StringUtils.defaultIfBlank(accessToken, "");
        String rToken = StringUtils.defaultIfBlank(refreshToken, "");

        if (aToken.isBlank() && rToken.isBlank()) {
            //needs authenticate
            throw new AuthException(QrpayErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        if (!jwtProvider.validateToken(aToken)) {
            JwtToken refresh = refresh(refreshToken);
            aToken = refresh.getAccessToken();
            rToken = refresh.getRefreshToken();
        }

        String memberId = jwtProvider.validateAndGetSubject(aToken);

        return ResponseAuthDto
                .builder()
                .memberId(memberId)
                .accessToken(aToken)
                .refreshToken(rToken)
                .build();
    }


}
