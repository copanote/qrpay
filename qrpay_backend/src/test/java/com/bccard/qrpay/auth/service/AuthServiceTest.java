package com.bccard.qrpay.auth.service;

import com.bccard.qrpay.auth.domain.JwtToken;
import com.bccard.qrpay.auth.domain.RefreshToken;
import com.bccard.qrpay.auth.repository.RefreshTokenQueryRepository;
import com.bccard.qrpay.auth.security.JwtProvider;
import com.bccard.qrpay.domain.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
// @Sql("classpath:sql/data.sql")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    RefreshTokenQueryRepository refreshTokenQueryRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @Rollback(value = false)
    void login_success() {

        // given
        String loginId = "kimsw131";
        String password = "112233";

        //        Member member = memberService.findMyLoginId(loginId);

        // when
        JwtToken jwtToken = null;

        // then
        assertThat(jwtToken).isNotNull();
        assertThat(jwtProvider.validateToken(jwtToken.getAccessToken())).isTrue();
        assertThat(jwtProvider.validateToken(jwtToken.getRefreshToken())).isTrue();
        Optional<RefreshToken> byToken = refreshTokenQueryRepository.findByTokenHash(
                refreshTokenService.hashRefreshToken(jwtToken.getRefreshToken()));
    }

    @Test
    @Rollback(value = false)
    void logout() {

        // given
        String loginId = "kimsw131";
        String password = "112233";

        //        Member member = memberService.findMyLoginId(loginId);

        // when
        JwtToken jwtToken = null;

        authService.logout(jwtToken.getRefreshToken());
    }

    @Test
    @Rollback(value = false)
    void test_refresh() {

        // given
        String loginId = "kimsw131";
        String password = "112233";

        //        Member member = memberService.findMyLoginId(loginId);

        // when
        JwtToken jwtToken = null;

        JwtToken refresh = authService.refresh(jwtToken.getRefreshToken());

        System.out.println(jwtToken.toString());
        System.out.println(refresh.toString());
    }
}
