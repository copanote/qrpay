package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum AcquisitionMethod implements DatabaseCodeConvertable {
    EDI("08"),
    EDC("41"),

    UNKNOWN(""),
    ;

    private final String dbCode;

    AcquisitionMethod(String dbCode) {
        this.dbCode = dbCode;
    }

    public static AcquisitionMethod of(String code) {
        for (AcquisitionMethod am : AcquisitionMethod.values()) {
            if (am.getDbCode().equalsIgnoreCase(code)) {
                return am;
            }
        }
        return UNKNOWN;
    }
}
