package com.bccard.qrpay.auth.service;

import com.bccard.qrpay.auth.domain.RefreshToken;
import com.bccard.qrpay.auth.repository.RefreshTokenQueryRepository;
import com.bccard.qrpay.auth.repository.RefreshTokenRepository;
import com.bccard.qrpay.auth.security.JwtProvider;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.code.AuthErrorCode;
import com.bccard.qrpay.utils.security.HashCipher;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenCudRepository;
    private final RefreshTokenQueryRepository refreshTokenQueryRepository;

    public String hashRefreshToken(String token) {
        return HashCipher.sha512EncodedBase64(token);
    }

    public RefreshToken findByHashToken(String hashToken) {
        RefreshToken rt = refreshTokenQueryRepository
                .findByTokenHash(hashToken)
                .orElseThrow(() -> new AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN));

        if (!rt.isValid()) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        return rt;
    }

    @Transactional
    public void revoke(String refreshToken, String revokeReason) {
        String hashed = hashRefreshToken(refreshToken);
        Optional<RefreshToken> optionalRefreshToken = refreshTokenQueryRepository.findByTokenHash(hashed);
        optionalRefreshToken.ifPresent(rt -> rt.revoke(revokeReason));
    }

    @Transactional
    public void refresh(String refreshToken) {
        String hashed = hashRefreshToken(refreshToken);
        Optional<RefreshToken> optionalRefreshToken = refreshTokenQueryRepository.findByTokenHash(hashed);
        optionalRefreshToken.ifPresent(RefreshToken::refresh);
    }
}
