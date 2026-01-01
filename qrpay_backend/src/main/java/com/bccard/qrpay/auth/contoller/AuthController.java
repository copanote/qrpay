package com.bccard.qrpay.auth.contoller;

import com.bccard.qrpay.auth.contoller.dto.RequestLogin;
import com.bccard.qrpay.auth.contoller.dto.ResponseRevoke;
import com.bccard.qrpay.auth.domain.JwtToken;
import com.bccard.qrpay.auth.service.AuthService;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping(value = "/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody @Validated RequestLogin requestLogin) {

        log.info("login={}", requestLogin);

//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(requestLogin.getLoginId(), requestLogin.getPassword());
//
//        Authentication authentication;
//        try {
//            authentication = authenticationManager.authenticate(authenticationToken);
//        } catch (BadCredentialsException e) {
//            log.error("BadCredentialsException={}", e, e.getMessage());
//            Member byLoginId = memberService.findByLoginId(requestLogin.getLoginId());
//            throw e;
//        } catch (UsernameNotFoundException e) {
//            // 아이디가 존재하지 않을 때
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디입니다.");
//        } catch (Exception e) {
//            // 기타 인증 관련 예외
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 중 오류가 발생했습니다.");
//        }
//        log.info("{}", authentication);
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

//        Member member = userDetails.qrpayMember();

        Member member = authService.login(requestLogin.getLoginId(), requestLogin.getPassword());
        JwtToken jwt = authService.createJwt(member.getMemberId(), member.getRole().toString());
        log.info("{}", jwt.toString());

        ResponseCookie cookie_ac = ResponseCookie.from("accessToken", jwt.getAccessToken())
                .httpOnly(false) // JavaScript 접근 방지 (XSS 공격 방어)
                .secure(false)   // HTTPS 통신에서만 전송 (Secure 속성)
                .path("/")      // 쿠키가 유효한 경로 설정 (전체 경로)
                .maxAge(60 * 30) // 쿠키 유효기간 설정 (예: 30분)
                .sameSite("Lax") // CSRF 방어를 위한 SameSite 설정 (선택 사항)
                .build();

        ResponseCookie cookie_rt = ResponseCookie.from("refreshToken", jwt.getRefreshToken())
                .httpOnly(false) // JavaScript 접근 방지 (XSS 공격 방어)
                .secure(false)   // HTTPS 통신에서만 전송 (Secure 속성)
                .path("/")      // 쿠키가 유효한 경로 설정 (전체 경로)
                .maxAge(60 * 30) // 쿠키 유효기간 설정 (예: 30분)
                .sameSite("Lax") // CSRF 방어를 위한 SameSite 설정 (선택 사항)
                .build();

        ResponseCookie cookie_memId = ResponseCookie.from("memId", member.getMemberId())
                .httpOnly(false) // JavaScript 접근 방지 (XSS 공격 방어)
                .secure(false)   // HTTPS 통신에서만 전송 (Secure 속성)
                .path("/")      // 쿠키가 유효한 경로 설정 (전체 경로)
                .maxAge(60 * 60 * 365) // 쿠키 유효기간 설정 (예: 1Year)
                .sameSite("Lax") // CSRF 방어를 위한 SameSite 설정 (선택 사항)
                .build();

        ResponseCookie cookie_loginId = ResponseCookie.from("loginId", member.getLoginId())
                .httpOnly(false) // JavaScript 접근 방지 (XSS 공격 방어)
                .secure(false)   // HTTPS 통신에서만 전송 (Secure 속성)
                .path("/")      // 쿠키가 유효한 경로 설정 (전체 경로)
                .maxAge(60 * 60 * 365) // 쿠키 유효기간 설정 (예: 1Year)
                .sameSite("Lax") // CSRF 방어를 위한 SameSite 설정 (선택 사항)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie_ac.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookie_rt.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookie_memId.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookie_loginId.toString());

        return ResponseEntity.ok().headers(headers).body(jwt);
    }

    @PostMapping(value = "/auth/refresh")
    @ResponseBody
    public ResponseEntity<?> refresh(@RequestBody JwtToken refreshToken) {
        JwtToken newAccessToken = authService.refresh(refreshToken.getRefreshToken());

        ResponseCookie cookie_ac = ResponseCookie.from("accessToken", newAccessToken.getAccessToken())
                .httpOnly(false) // JavaScript 접근 방지 (XSS 공격 방어)
                .secure(false)   // HTTPS 통신에서만 전송 (Secure 속성)
                .path("/")      // 쿠키가 유효한 경로 설정 (전체 경로)
                .maxAge(60 * 30) // 쿠키 유효기간 설정 (예: 30분)
                .sameSite("Lax") // CSRF 방어를 위한 SameSite 설정 (선택 사항)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie_ac.toString());

        return ResponseEntity.ok().headers(headers).body(newAccessToken);
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
    public ResponseEntity<?> revoke(@RequestBody JwtToken refreshToken) {
        /*
        (옵션) POST /auth/revoke 관리자용: 특정 토큰/세션 무효화
         */
        log.info("{}", refreshToken);
        authService.revoke(refreshToken.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/auth/revoke/merchant/{merchantId}")
    @ResponseBody
    public ResponseEntity<?> revokeAll(@PathVariable("merchantId") String merchantId) {

        if (merchantId == null || merchantId.isEmpty()) {
            return ResponseEntity.ok(ResponseRevoke.builder().count(0).build());
        }
        int cnt = authService.revokeAllByMerchant(merchantId, "ADMINI_REVOKE");

        return ResponseEntity.ok(ResponseRevoke.builder().count(cnt).build());
    }


}
