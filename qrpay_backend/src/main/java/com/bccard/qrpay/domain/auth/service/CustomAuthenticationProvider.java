package com.bccard.qrpay.domain.auth.service;

import com.bccard.qrpay.config.security.CustomPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomPasswordEncoder customPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails u = customUserDetailsService.loadUserByUsername(username);

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
