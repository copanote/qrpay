package com.bccard.qrpay.domain.log.repository;

import com.bccard.qrpay.domain.log.QQrpayLog;
import com.bccard.qrpay.domain.log.QrpayLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class QrpayLogQueryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    public QrpayLogQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    private static final QQrpayLog qrpayLog = QQrpayLog.qrpayLog;

    public Long getNextSequenceValue() {
        String sql = "SELECT BCDBA.SEQ_MPMQRPAYPSTCLOG.NEXTVAL FROM DUAL";
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    public Optional<QrpayLog> findById(Long id) {
        QrpayLog q = queryFactory.selectFrom(qrpayLog).where(qrpayLog.id.eq(id)).fetchFirst();

        return Optional.ofNullable(q);
    }
}
