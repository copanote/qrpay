package com.bccard.qrpay.domain.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QrKitShippingStatus implements DatabaseCodeConvertable {
    APPLIED("10", "신청접수완료"),
    PREPARING("20", "업체요청완료"),
    SHIPPED("30", "업체등기발송완료"),
    ;

    private final String dbCode;
    private final String desc;

    public static QrKitShippingStatus findByCode(String code) {
        for (QrKitShippingStatus status : QrKitShippingStatus.values()) {
            if (status.getDbCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }
}
