package com.bccard.qrpay.config.security;

import com.bccard.qrpay.exception.ApiError;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.info("accessDeniedException:{}", accessDeniedException.toString());

        QrpayErrorCode ec = QrpayErrorCode.INVALID_AUTHORIZATION;

        ApiError apiError =
                ApiError.builder().code(ec.getCode()).message(ec.getMessage()).build();

        String json = objectMapper.writeValueAsString(apiError);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(json);
    }
}
