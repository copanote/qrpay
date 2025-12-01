package com.bccard.qrpay.domain.transaction;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionQueryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    public TransactionQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    private static final QTransaction transaction = QTransaction.transaction;

    public Optional<Transaction> findById(TransactionId id) {
        Transaction t = queryFactory
                .selectFrom(transaction)
                .where(transaction
                        .transactionId
                        .eq(id.getTransactionId())
                        .and(transaction.affiliateTransactionId.eq(id.getAffiliateTransactionId()))
                        .and(transaction.authorizeType.eq(id.getAuthorizeType())))
                .fetchFirst();
        return Optional.ofNullable(t);
    }
}
