package com.bccard.qrpay.exception;

import com.bccard.qrpay.exception.code.MerchantErrorCode;

public class MerchantException extends QrpayCustomException {
    public MerchantException(MerchantErrorCode errorCode) {
        super(errorCode);
    }
}
