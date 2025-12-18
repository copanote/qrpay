package com.bccard.qrpay.exception.handler;


import com.bccard.qrpay.exception.ApiError;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.ErrorCode;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {

    // 500 Internal Server Error: 기타 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception e) {

        ErrorCode errorCode = QrpayErrorCode.SYSTEM_ERROR;

        ApiError apiError = ApiError.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        log.error("Internal Server Error: {}", e.getMessage(), e);

        return ResponseEntity.status(errorCode.getStatus()).body(apiError);
    }

    // 500 Internal Server Error: 기타 예상치 못한 모든 예외 처리
    @ExceptionHandler(QrpayCustomException.class)
    public ResponseEntity<ApiError> handleQrpayCustomExceptions(QrpayCustomException e) {

        ErrorCode errorCode = e.getErrorCode();

        ApiError apiError = ApiError.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        log.error("QrpayCustomException={}", e.getMessage(), e);
        return ResponseEntity.status(errorCode.getStatus()).body(apiError);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiError> handleAuthException(AuthException e) {

        ErrorCode errorCode = e.getErrorCode();

        ApiError apiError = ApiError.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        log.error("AuthException: {}", e.getMessage(), e);

        return ResponseEntity.status(errorCode.getStatus()).body(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException e) {

        ErrorCode errorCode = QrpayErrorCode.INVALID_CREDENTIAL;

        ApiError apiError = ApiError.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        log.error("BadCredentialsException: {}", e.getMessage(), e);

        return ResponseEntity.status(errorCode.getStatus()).body(apiError);
    }


}
