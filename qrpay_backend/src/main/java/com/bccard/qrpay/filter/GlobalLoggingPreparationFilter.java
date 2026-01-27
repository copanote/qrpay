package com.bccard.qrpay.filter;

import com.bccard.qrpay.domain.log.QrpayLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;


@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalLoggingPreparationFilter extends OncePerRequestFilter {
    private final QrpayLogService apiLogService;
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    public GlobalLoggingPreparationFilter(QrpayLogService apiLogService) {
        this.apiLogService = apiLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Correlation ID 생성 및 MDC 설정
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_KEY, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        // 2. 바디 캐싱을 위한 Wrapper 적용
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            // 이후 필터(Security 등) 및 컨트롤러 실행
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 3. 응답 바디를 실제 응답 스트림에 복사 (필수!)
            responseWrapper.copyBodyToResponse();

            MDC.clear();
        }
    }

}
