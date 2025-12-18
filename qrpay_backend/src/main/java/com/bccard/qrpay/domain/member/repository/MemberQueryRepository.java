package com.bccard.qrpay.domain.member.repository;

import com.bccard.qrpay.domain.device.QDevice;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.QMember;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.QFinancialInstitutionMerchant;
import com.bccard.qrpay.domain.merchant.QMerchant;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public MemberQueryRepository(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    private static final QMember member = QMember.member;
    private static final QMerchant merchant = QMerchant.merchant;
    private static final QFinancialInstitutionMerchant financialInstitutionMerchant = QFinancialInstitutionMerchant.financialInstitutionMerchant;
    private static final QDevice device = QDevice.device;

    public Long getNextSequenceValue() {
        String sql = "SELECT BCDBA.SEQ_MPMMERCDHDINFO.NEXTVAL FROM DUAL";
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    public Optional<Member> findById(String memberId) {
        Member m = queryFactory
                .selectFrom(member)
                .innerJoin(member.merchant, merchant).fetchJoin()
                .leftJoin(merchant.fiMerchants, financialInstitutionMerchant).fetchJoin()
                .leftJoin(member.device, device).fetchJoin()
                .where(member.memberId.eq(memberId))
                .fetchFirst();
        return Optional.ofNullable(m);
    }

    public Optional<Member> findByLoginId(String loginId) {
        Member m = queryFactory
                .selectFrom(member)
                .innerJoin(member.merchant, merchant).fetchJoin()
                .leftJoin(merchant.fiMerchants, financialInstitutionMerchant).fetchJoin()
                .leftJoin(member.device, device).fetchJoin()
                .where(member.loginId.eq(loginId))
                .fetchFirst();
        return Optional.ofNullable(m);
    }
    
    public List<Member> findAllMembers(Merchant m) {
        return queryFactory.selectFrom(member)
                .innerJoin(member.merchant, merchant).fetchJoin()
                .leftJoin(merchant.fiMerchants, financialInstitutionMerchant).fetchJoin()
                .leftJoin(member.device, device).fetchJoin()
                .where(member.merchant.eq(m))
                .fetch();
    }

}
