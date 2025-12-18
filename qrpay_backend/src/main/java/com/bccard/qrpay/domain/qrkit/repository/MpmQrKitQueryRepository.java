package com.bccard.qrpay.domain.qrkit.repository;

import com.bccard.qrpay.domain.qrkit.MpmQrKitApplication;
import com.bccard.qrpay.domain.qrkit.QMpmQrKitApplication;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class MpmQrKitQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    private static final QMpmQrKitApplication mpmQrKitApplication = QMpmQrKitApplication.mpmQrKitApplication;

    public MpmQrKitQueryRepository(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    public Long getNextSequenceValue() {
        //TODO
        String sql = "SELECT BCDBA.SEQ_MPMQRSNDAPLC.NEXTVAL FROM DUAL";
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    public List<MpmQrKitApplication> findByMerchantId(String merchantId) {
        return queryFactory.selectFrom(mpmQrKitApplication)
                .where(mpmQrKitApplication.merchantId.eq(merchantId))
                .orderBy(mpmQrKitApplication.createdAt.desc()).fetch();
    }

}
