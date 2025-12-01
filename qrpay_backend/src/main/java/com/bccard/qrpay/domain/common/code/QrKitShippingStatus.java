package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum QrKitShippingStatus implements DatabaseCodeConvertable {
    APPLICATION_RECEIVED("10", "신청접수완료"),
    VENDOR_REQUEST_COMPLITE("20", "업체요청완료"),
    VENDOR_SHIPPED_REGISTERED("30", "업체등기발송완료"),
    ;

    private String dbCode;
    private String desc;

    QrKitShippingStatus(String dbCode, String desc) {
        this.dbCode = dbCode;
        this.desc = desc;
    }

    public static QrKitShippingStatus findByCode(String code) {
        for (QrKitShippingStatus status : QrKitShippingStatus.values()) {
            if (status.getDbCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }
}
