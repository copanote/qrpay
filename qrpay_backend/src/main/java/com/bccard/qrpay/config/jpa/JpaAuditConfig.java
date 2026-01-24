package com.bccard.qrpay.config.jpa;

import com.bccard.qrpay.domain.auth.CustomUserDetails;
import com.bccard.qrpay.domain.member.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
                return Optional.of("QRPAY_AUDITOR");
            }
            Member member = userDetails.qrpayMember();
            return Optional.of(member.getMemberId());
        };
    }
}
