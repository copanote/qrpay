package com.bccard.qrpay.domain.member.repository;

import com.bccard.qrpay.domain.member.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, String> {

    //        String paddedSeq = StringUtils.leftPad(sequence.toString(), 8, '0');

    @Modifying
    @Transactional
    @Query("UPDATE Member m " + "SET m.status = com.bccard.qrpay.domain.common.code.MemberStatus.CANCELLED, "
            + "    m.withdrawalAt = :withdrawalAt "
            + "WHERE m.merchant.merchantId = :merchantId "
            + "AND m.status <> com.bccard.qrpay.domain.common.code.MemberStatus.CANCELLED")
    int updateStatusToCancelByMerchantId(
            @Param("merchantId") String merchantId, @Param("withdrawalAt") String withdrawalAt);
}
