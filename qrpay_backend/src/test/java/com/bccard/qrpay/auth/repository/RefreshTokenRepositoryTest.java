package com.bccard.qrpay.auth.repository;

import com.bccard.qrpay.domain.auth.RefreshToken;
import com.bccard.qrpay.domain.auth.repository.RefreshTokenQueryRepository;
import com.bccard.qrpay.domain.auth.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class RefreshTokenRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    RefreshTokenQueryRepository refreshTokenQueryRepository;

    @Test
    void test_create() {
        RefreshToken rt = RefreshToken.createNew()
                .memberId("0001")
                .tokenHash("tokenhash")
                .issuedAt(Instant.now().toEpochMilli())
                .expiresAt(Instant.now().toEpochMilli())
                .deviceId("diviceId")
                .build();

        refreshTokenRepository.save(rt);

        em.flush();

        Optional<RefreshToken> tokenhash = refreshTokenQueryRepository.findByTokenHash("tokenhash");
        System.out.println(tokenhash.get().getCreatedAt());
    }

    @Test
    void test_revoke() {
        refreshTokenRepository.revokeAllRefreshTokenByMemberId("111", 11L, "test");
        em.flush();
    }
}
