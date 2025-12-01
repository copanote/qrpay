package com.bccard.qrpay.exception;

import com.bccard.qrpay.exception.code.MemberErrorCode;

public class MemberException extends QrpayCustomException {
    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
