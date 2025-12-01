package com.bccard.qrpay.auth.service;

import com.bccard.qrpay.auth.domain.CustomUserDetails;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.repository.MemberQueryRepository;
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
        log.info("{}", member);

        return CustomUserDetails.of(member);
    }

    public UserDetails loadUserByMemberId(String memberId) throws UsernameNotFoundException {
        Member member = memberQueryRepository
                .findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("NotFound Username"));
        return CustomUserDetails.of(member);
    }
}
