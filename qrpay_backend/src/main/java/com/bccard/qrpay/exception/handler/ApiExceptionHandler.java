package com.bccard.qrpay.exception.handler;


import com.bccard.qrpay.exception.ApiError;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.ErrorCode;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

    /**
     * @Valid 또는 @Validated 사용 시 객체 바인딩 에러 처리 (DTO validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 첫 번째 에러 메시지를 가져오거나 전체를 결합합니다.
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorCode errorCode = QrpayErrorCode.INVALID_PARAMETER;

        ApiError apiError = ApiError.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorMessage)
                .build();

        log.error("Internal Server Error: {}", e.getMessage(), e);
        return ResponseEntity.status(errorCode.getStatus()).body(apiError);
    }


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
