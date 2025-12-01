package com.bccard.qrpay.auth.repository;


import com.bccard.qrpay.auth.domain.RefreshToken;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@SpringBootTest
@Transactional
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


}
