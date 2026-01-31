package com.bccard.qrpay.domain.merchant.application.service;

import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.repository.MemberQueryRepository;
import com.bccard.qrpay.domain.member.repository.MemberSearchCondition;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.application.port.in.SearchMerchantMemberCommand;
import com.bccard.qrpay.domain.merchant.application.port.in.SearchMerchantMemberUseCase;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class SearchMerchantMemberService implements SearchMerchantMemberUseCase {

    private final MerchantQueryRepository merchantQueryRepository;
    private final MemberQueryRepository memberQueryRepository;


    @Override
    @Transactional(readOnly = true)
    public List<Member> find(SearchMerchantMemberCommand command) {

        Objects.requireNonNull(command, "SearchMerchantMemberCommands cannot be null");
        Objects.requireNonNull(command.merchantId(), "merchantId cannot be null");
        
        Merchant fetchedMerchant = merchantQueryRepository
                .findById(command.merchantId())
                .orElseThrow(() -> new MerchantException(QrpayErrorCode.MERCHANT_NOT_FOUND));

        MemberSearchCondition condition = MemberSearchCondition.builder()
                .merchantId(fetchedMerchant.getMerchantId())
                .status(command.status())
                .role(command.role())
                .build();

        return memberQueryRepository.findMembersBy(condition);
    }
}
