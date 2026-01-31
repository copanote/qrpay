package com.bccard.qrpay.domain.mpmqr.repository;

import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.domain.mpmqr.QMpmQrPublication;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
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

    public String createQrReferenceId() {
        String sql = "SELECT BCDBA.SEQ_MPMQRCRETCTNT.NEXTVAL FROM DUAL";
        Object result = entityManager.createNativeQuery(sql).getSingleResult();

        String paddedSeq = StringUtils.leftPad(String.valueOf(((Number) result).longValue()), 9, '0');
        String yyyyString = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.FORMATTER_yyyy);
        return "MQ" + yyyyString + paddedSeq;
    }

    public Optional<MpmQrPublication> findById(String qrReferenceId) {
        MpmQrPublication m = queryFactory
                .selectFrom(mpmQrPublication)
                .where(mpmQrPublication.qrReferenceId.eq(qrReferenceId))
                .fetchFirst();
        return Optional.ofNullable(m);
    }

    public Optional<MpmQrPublication> findNewestStaticMpmQr(Merchant m) {
        MpmQrPublication staticMpmQr = queryFactory
                .selectFrom(mpmQrPublication)
                .where(mpmQrPublication.merchant.eq(m).and(mpmQrPublication.pim.eq(PointOfInitMethod.STATIC)))
                .orderBy(mpmQrPublication.createdAt.desc())
                .fetchFirst();
        return Optional.ofNullable(staticMpmQr);
    }
}
