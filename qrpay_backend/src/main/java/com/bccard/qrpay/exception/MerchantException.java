package com.bccard.qrpay.exception;

import com.bccard.qrpay.exception.code.ErrorCode;

public class MerchantException extends QrpayCustomException {
    public MerchantException(ErrorCode errorCode) {
        super(errorCode);
    }
}
