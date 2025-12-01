package com.bccard.qrpay.domain.mpmqr.repository;

import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.QMpmQrPublication;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MpmQrPublicationQueryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    public MpmQrPublicationQueryRepository(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    private static final QMpmQrPublication mpmQrPublication = QMpmQrPublication.mpmQrPublication;

    public Long getNextSequenceValue() {
        String sql = "SELECT BCDBA.SEQ_MPMQRCRETCTNT.NEXTVAL FROM DUAL";
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    public Optional<MpmQrPublication> findById(String qrReferenceId) {
        MpmQrPublication m = queryFactory
                .selectFrom(mpmQrPublication)
                .where(mpmQrPublication.qrReferenceId.eq(qrReferenceId))
                .fetchFirst();
        return Optional.ofNullable(m);
    }
}
