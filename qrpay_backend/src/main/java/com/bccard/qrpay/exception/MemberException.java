package com.bccard.qrpay.exception;

import com.bccard.qrpay.exception.code.ErrorCode;

public class MemberException extends QrpayCustomException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
