package com.bccard.qrpay.domain.auth.service;

import com.bccard.qrpay.domain.auth.CustomUserDetails;
import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.common.code.MerchantStatus;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.repository.MemberQueryRepository;
import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberQueryRepository memberQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberQueryRepository
                .findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("NotFound Username"));

        if (member.getStatus() == MemberStatus.CANCELLED) {
            throw new QrpayCustomException(QrpayErrorCode.MEMBER_CANCELED);
        }

        if (member.getStatus() == MemberStatus.SUSPENDED) {
            throw new QrpayCustomException(QrpayErrorCode.MEMBER_SUSPENDED);
        }

        if (MerchantStatus.CANCELLED == member.getMerchant().getMerchantStatus()) {
            throw new QrpayCustomException(QrpayErrorCode.MERCHANT_CANCELED);
        }

        return CustomUserDetails.of(member);
    }

    public UserDetails loadUserByMemberId(String memberId) throws UsernameNotFoundException {
        Member member = memberQueryRepository
                .findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("NotFound Username"));

        if (member.getStatus() == MemberStatus.CANCELLED) {
            throw new QrpayCustomException(QrpayErrorCode.MEMBER_CANCELED);
        }

        if (member.getStatus() == MemberStatus.SUSPENDED) {
            throw new QrpayCustomException(QrpayErrorCode.MEMBER_SUSPENDED);
        }

        if (MerchantStatus.CANCELLED == member.getMerchant().getMerchantStatus()) {
            throw new QrpayCustomException(QrpayErrorCode.MERCHANT_CANCELED);
        }

        return CustomUserDetails.of(member);
    }
}
