package com.bccard.qrpay.domain.merchant.repository;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.QFinancialInstitutionMerchant;
import com.bccard.qrpay.domain.merchant.QMerchant;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MerchantQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    public MerchantQueryRepository(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    private static final QMerchant merchant = QMerchant.merchant;
    private static final QFinancialInstitutionMerchant financialInstitutionMerchant =
            QFinancialInstitutionMerchant.financialInstitutionMerchant;

    public Long getNextSequenceValue() {
        String sql = "SELECT BCDBA.SEQ_MPMMERBASINFO.NEXTVAL FROM DUAL";
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    public List<Merchant> findAll() {
        return queryFactory.selectFrom(merchant).fetch();
    }

    public Optional<Merchant> findById(String merchantId) {
        Merchant m = queryFactory
                .selectFrom(merchant)
                .leftJoin(merchant.fiMerchants, financialInstitutionMerchant)
                .fetchJoin()
                .where(merchant.merchantId.eq(merchantId))
                .fetchOne();
        return Optional.ofNullable(m);
    }

    public Optional<Merchant> findByFinancialInstitutionAndFiMerchantNo(
            FinancialInstitution institution, String fiMerchantNo) {
        Merchant m = queryFactory
                .selectFrom(merchant)
                .leftJoin(merchant.fiMerchants, financialInstitutionMerchant)
                .fetchJoin()
                .where(financialInstitutionMerchant
                        .financialInstitution
                        .eq(institution)
                        .and(financialInstitutionMerchant.fiMerchantNo.eq(fiMerchantNo)))
                .fetchFirst();
        return Optional.ofNullable(m);
    }
}
