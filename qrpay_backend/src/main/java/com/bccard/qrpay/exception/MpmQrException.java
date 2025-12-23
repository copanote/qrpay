package com.bccard.qrpay.exception;

import com.bccard.qrpay.exception.code.ErrorCode;

public class MpmQrException extends QrpayCustomException {
    public MpmQrException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public MpmQrException(ErrorCode errorCode) {
        super(errorCode);
    }
}
