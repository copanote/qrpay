package com.bccard.qrpay.domain.merchant.application.port.in;

import com.bccard.qrpay.domain.member.Member;

import java.util.List;

/**
 * 가맹점의 회원 조회(검색) UseCase
 */
public interface SearchMerchantMemberUseCase {
    List<Member> find(SearchMerchantMemberCommand command);
}
