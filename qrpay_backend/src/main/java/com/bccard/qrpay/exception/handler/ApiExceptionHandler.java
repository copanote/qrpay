package com.bccard.qrpay.exception.handler;


import com.bccard.qrpay.exception.ApiError;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.code.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {

    // 500 Internal Server Error: 기타 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiError apiError = ApiError.builder()
                .code("500")
                .message(e.getMessage())
                .build();

        log.error("Internal Server Error: {}", e.getMessage(), e);

        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiError> handleAuthException(AuthException e) {

        ErrorCode errorCode = e.getErrorCode();

        ApiError apiError = ApiError.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        log.error("AuthException: {}", e.getMessage(), e);

        return ResponseEntity.status(errorCode.getStatus()).body(apiError);
    }


}
