package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum MerchantStatus implements DatabaseCodeConvertable {
    ACTIVE("00"),
    SUSPENDED("55"),
    CANCELLED("99"),

    UNKNOWN("");

    private final String dbCode;

    MerchantStatus(String dbCode) {
        this.dbCode = dbCode;
    }

    public static MerchantStatus of(String code) {
        for (MerchantStatus ms : MerchantStatus.values()) {
            if (ms.getDbCode().equalsIgnoreCase(code)) {
                return ms;
            }
        }
        return UNKNOWN;
    }
}
