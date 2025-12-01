package com.bccard.qrpay.auth.contoller;


import com.bccard.qrpay.auth.contoller.dto.RequestLogin;
import com.bccard.qrpay.auth.domain.CustomUserDetails;
import com.bccard.qrpay.auth.domain.JwtToken;
import com.bccard.qrpay.auth.service.AuthService;
import com.bccard.qrpay.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping(value = "/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody @Validated RequestLogin requestLogin) {

        log.info("login={}", requestLogin);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestLogin.getLoginId(), requestLogin.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        log.info("{}", authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Member member = userDetails.qrpayMember();
        JwtToken jwt = authService.createJwt(member.getMemberId(), member.getRole().toString());
        log.info("{}", jwt.toString());

        return ResponseEntity.ok(jwt);
    }

    @PostMapping(value = "/auth/refresh")
    @ResponseBody
    public ResponseEntity<?> refresh(@RequestBody JwtToken refreshToken) {
        JwtToken newAccessToken = authService.refresh(refreshToken.getRefreshToken());
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping(value = "/auth/logout")
    @ResponseBody
    public ResponseEntity<?> logout(@RequestBody JwtToken refreshToken) {
        /*
        Request: refresh token (cookie or body)
        Action: revoke refresh token
        */

        log.info("{}", refreshToken);
        authService.logout(refreshToken.getRefreshToken());
        //history

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/auth/me")
    @ResponseBody
    public void me() {
        /*
        Request: Authorization: Bearer accessToken
        Response: 사용자 정보
         */
    }

    @PostMapping(value = "/auth/revoke")
    @ResponseBody
    public void revoke(@RequestBody JwtToken refreshToken) {
        /*
        (옵션) POST /auth/revoke 관리자용: 특정 토큰/세션 무효화
         */
        log.info("{}", refreshToken);
        authService.revoke(refreshToken.getRefreshToken());
    }
}
