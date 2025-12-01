package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum QrTransactionType implements DatabaseCodeConvertable {
    CUSTOMER_AMOUNT_QR("CA", "고객금액설정"),
    MERCHANT_AMOUNT_QR("MA", "가맹점금액설정"),
    SMART_ORDER("SO", "메뉴판결제-스마트오더"),
    UNKNOWN("", "");

    private final String dbCode;
    private final String description;

    QrTransactionType(String dbCode, String description) {
        this.dbCode = dbCode;
        this.description = description;
    }

    public static QrTransactionType of(String code) {
        for (QrTransactionType qt : QrTransactionType.values()) {
            if (qt.getDbCode().equalsIgnoreCase(code)) {
                return qt;
            }
        }
        return UNKNOWN;
    }
}
