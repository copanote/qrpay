package com.bccard.qrpay.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoginRedirectInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 쿠키에서 회원 정보 확인
        Cookie[] cookies = request.getCookies();
        String memId = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("memId".equals(c.getName())) memId = c.getValue();
            }
        }

        // 로그인 안 되어 있으면 리다이렉트 후 false 반환
        if (memId == null) {
            String currentUri = request.getRequestURI();
            response.sendRedirect("/pages/login?redirectURL=" + currentUri);
            return false;
        }
        return true;
    }


}
