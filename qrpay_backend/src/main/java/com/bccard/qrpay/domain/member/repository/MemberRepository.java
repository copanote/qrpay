package com.bccard.qrpay.domain.member.repository;

import com.bccard.qrpay.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    //        String paddedSeq = StringUtils.leftPad(sequence.toString(), 8, '0');

}
