package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum ServiceType implements DatabaseCodeConvertable {
    BC("B", "BC카드"),
    UPI("U", "UPI Outgoing"),
    KB("K", "KB카드"),
    SHINHAN("S", "신한카드"),
    HANA("H", "BC카드"),
    LOTTE("L", "롯데카드");

    private final String dbCode;
    private final String description;

    ServiceType(String dbCode, String description) {
        this.dbCode = dbCode;
        this.description = description;
    }
}
