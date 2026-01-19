package com.bccard.qrpay.exception;

import com.bccard.qrpay.exception.code.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class QrpayCustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public QrpayCustomException(ErrorCode errorCode) {
        super("[" + errorCode.getStatus() + ", " + errorCode.getCode() + ", " + errorCode.getMessage() + "]");
        this.errorCode = errorCode;
    }

    public QrpayCustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public QrpayCustomException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public QrpayCustomException(Throwable cause, ErrorCode errorCode) {
        super("[" + errorCode.getStatus() + ", " + errorCode.getCode() + ", " + errorCode.getMessage() + "]", cause);
        this.errorCode = errorCode;
    }
}
