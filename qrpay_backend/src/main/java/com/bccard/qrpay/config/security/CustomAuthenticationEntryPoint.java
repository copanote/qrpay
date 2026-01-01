package com.bccard.qrpay.config.security;

import com.bccard.qrpay.exception.ApiError;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("authException={}", authException, authException.getCause());

        QrpayErrorCode ec = QrpayErrorCode.AUTHENTICATE_REQUIRED;

        ApiError apiError = ApiError.builder()
                .code(ec.getCode())
                .message(ec.getMessage())
                .build();

        String json = objectMapper.writeValueAsString(apiError);
        response.setStatus(ec.getStatus());
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(json);
    }
}
