package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum PointOfInitMethod implements DatabaseCodeConvertable {
    STATIC("11"),
    DYNAMIC("12"),

    UNKNOWN(""),
    ;

    private final String dbCode;

    PointOfInitMethod(String dbCode) {
        this.dbCode = dbCode;
    }

    public static PointOfInitMethod of(String code) {
        for (PointOfInitMethod pim : PointOfInitMethod.values()) {
            if (pim.getDbCode().equalsIgnoreCase(code)) {
                return pim;
            }
        }
        return UNKNOWN;
    }
}
