package com.bccard.qrpay.auth.repository;

import com.bccard.qrpay.auth.domain.QRefreshToken;
import com.bccard.qrpay.auth.domain.RefreshToken;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RefreshTokenQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public RefreshTokenQueryRepository(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    private static final QRefreshToken refreshToken = QRefreshToken.refreshToken;


    public String uuid() {
        return UUID.randomUUID().toString();
    }


    public Optional<RefreshToken> findById(Long id) {
        RefreshToken rt = queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.id.eq(id))
                .fetchFirst();
        return Optional.ofNullable(rt);
    }

    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        RefreshToken rt = queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.tokenHash.eq(tokenHash))
                .fetchFirst();
        return Optional.ofNullable(rt);
    }

}
