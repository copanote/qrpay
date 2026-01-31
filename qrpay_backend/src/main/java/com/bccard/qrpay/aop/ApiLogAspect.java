package com.bccard.qrpay.aop;

import com.bccard.qrpay.domain.log.QrpayLog;
import com.bccard.qrpay.domain.log.QrpayLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiLogAspect {

    private final QrpayLogService apiLogService;
    private final ObjectMapper objectMapper;

    @Around("execution(* com.bccard.qrpay.controller.api..*.*(..))") // 컨트롤러 범위 지정
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 1. 로직 실행
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {

            long duration = System.currentTimeMillis() - start;

            // 2. 요청/응답 정보 추출
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

            QrpayLog qrpayLog = QrpayLog.restApiLog(apiLogService.getId(),
                    MDC.get("correlationId"),
                    request.getRequestURI(),
                    response.getStatus() + "",
                    request.getQueryString() + "|" + extractHeaders(request) + "|" + extractRequestBody(request),
                    objectMapper.writeValueAsString(result)
            );

            // 4. 비동기 저장 호출
            apiLogService.saveApiLog(qrpayLog);

        }
    }

    private String extractHeaders(HttpServletRequest request) {

        List<String> blackList = Arrays.asList("authorization", "cookie", "proxy-authorization");

        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String lowerName = name.toLowerCase();

            if (blackList.contains(lowerName)) {
                headerMap.put(name, "***"); // 값을 마스킹 처리
            } else {
                headerMap.put(name, request.getHeader(name));
            }
        }
        return headerMap.toString();
    }

    private String extractRequestBody(HttpServletRequest request) {

        ContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);

        if (wrapper != null) {
            log.info(wrapper.getContentType());
            byte[] buf = wrapper.getContentAsByteArray();
            log.info(String.valueOf(buf.length));
            if (buf.length > 0) return new String(buf, StandardCharsets.UTF_8);
        }
        return "";
    }

    private String extractResponseBody(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);


        log.info("response wrapper{}", wrapper.getContentType());

        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) return new String(buf, StandardCharsets.UTF_8);
        }
        return "";
    }


}