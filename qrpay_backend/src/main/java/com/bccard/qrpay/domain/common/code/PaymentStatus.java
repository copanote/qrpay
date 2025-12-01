package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum PaymentStatus implements DatabaseCodeConvertable {
    APPROVED("15", "정상승인"),

    AUTHORIZE_REQUEST("10", "승인요청"),
    AUTHORIZE_REVERSAL("18", "승인REVERSAL"),
    AUTHORIZE_REJECTED("19", "승인거절"),

    CANCELED("95", "정상취소"),
    CANCELED_REQUEST("90", "취소요청"),
    CANCELED_REJECTED("99", "취소거절"),

    UNKNOWN("00", "Unknown"),
    ;

    private final String dbCode;
    private final String description;

    PaymentStatus(String dbCode, String description) {
        this.dbCode = dbCode;
        this.description = description;
    }

    public static PaymentStatus of(String code) {
        for (PaymentStatus st : PaymentStatus.values()) {
            if (st.getDbCode().equalsIgnoreCase(code)) {
                return st;
            }
        }
        return UNKNOWN;
    }
}
