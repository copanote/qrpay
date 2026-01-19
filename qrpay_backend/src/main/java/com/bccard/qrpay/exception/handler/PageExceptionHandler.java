package com.bccard.qrpay.exception.handler;

import com.bccard.qrpay.exception.ApiError;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class PageExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.info("ControllerAdvice");
        log.error("error", ex);
        model.addAttribute("error", "데이터가 존재하지 않습니다.");
        return "error/404"; // error/400.html (또는 .jsp) 뷰 반환
    }

    // 500 Internal Server Error: 기타 예상치 못한 모든 예외 처리
    @ExceptionHandler(QrpayCustomException.class)
    public String handleQrpayCustomException(QrpayCustomException e, Model model) {

        ErrorCode errorCode = e.getErrorCode();

        ApiError apiError = ApiError.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        log.error("QrpayCustomException={}", e.getMessage(), e);
        return "error/404"; // error/400.html (또는 .jsp) 뷰 반환
    }

    @ExceptionHandler(AuthException.class)
    public String handleAuthException(AuthException e, Model model) {

        ErrorCode errorCode = e.getErrorCode();

        ApiError apiError = ApiError.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        log.error("AuthException: {}", e.getMessage(), e);
        return "error/404"; // error/400.html (또는 .jsp) 뷰 반환
    }
}
