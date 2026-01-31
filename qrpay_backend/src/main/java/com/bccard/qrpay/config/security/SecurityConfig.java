package com.bccard.qrpay.config.security;

import com.bccard.qrpay.domain.auth.service.CustomUserDetailsService;
import com.bccard.qrpay.filter.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomPasswordEncoder customPasswordEncoder;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web ->
                web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())
                .sessionManagement(
                        sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req.requestMatchers("/auth/**")
                        .permitAll()
                        .requestMatchers("/pages/**")
                        .permitAll()
                        .requestMatchers("/qrpay/api/open/**")
                        .permitAll()
                        .requestMatchers("/external/**")
                        .permitAll()
                        .requestMatchers("/error")
                        .permitAll()
                        .requestMatchers("/.well-known/**")
                        .permitAll() // 브라우저 자동호출 차단
                        .requestMatchers("/fonts/**")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()
                        .requestMatchers("/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(h -> h.authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authenticationManager(authenticationManager(http));

        // JWT 인증 필터 추가
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // **Access-Control-Allow-Origin** 헤더에 해당하는 출처 설정
        //        configuration.setAllowedOrigins(List.of("*"));
        // 와일드카드 *를 사용할 수 있지만, `allowCredentials`가 true이면 특정 출처를 명시해야 합니다.
        configuration.setAllowedOriginPatterns(List.of("*")); // 패턴 사용 예시

        // 허용할 HTTP 메서드 (Access-Control-Allow-Methods)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 헤더 (Access-Control-Allow-Headers)
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용

        // 자격 증명(쿠키, 인증 헤더) 허용 여부 (Access-Control-Allow-Credentials)
        configuration.setAllowCredentials(true);

        // Preflight 요청 결과 캐시 시간 (Access-Control-Max-Age)
        configuration.setMaxAge(3600L); // 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 CORS 설정을 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        //        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        //        builder.userDetailsService(userDetailsService).passwordEncoder(customPasswordEncoder);
        //        return builder.build();

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(customPasswordEncoder); // Delegating 사용 X
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return customPasswordEncoder;
    }
}
