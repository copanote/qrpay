package com.bccard.qrpay.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MerchantErrorCode implements ErrorCode {

    MERCHANT_NOT_FOUND(404, "", "가맹점정보를 찾을 수 없습니다."),
    MERCHANT_CANCELED(400, "", "탈회한 가맹점"),
    ;

    private final int status;
    private final String code;
    private final String message;

}
