package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum PointOfInitMethod implements DatabaseCodeConvertable {
    STATIC("11", "고정형(금액없음)"),
    DYNAMIC("12", "변동형(금액존재)"),
    UNKNOWN("", ""),
    ;

    private final String dbCode;
    private final String desc;

    PointOfInitMethod(String dbCode, String desc) {
        this.dbCode = dbCode;
        this.desc = desc;
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
