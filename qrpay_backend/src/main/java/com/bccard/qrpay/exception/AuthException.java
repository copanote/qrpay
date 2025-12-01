package com.bccard.qrpay.exception;

import com.bccard.qrpay.exception.code.ErrorCode;

public class AuthException extends QrpayCustomException {
    public AuthException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
