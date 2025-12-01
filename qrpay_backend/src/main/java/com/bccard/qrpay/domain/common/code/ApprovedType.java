package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum ApprovedType implements DatabaseCodeConvertable {
    LUMPSUM_AUTH("05"),
    INSTALLMENT_AUTH("08"),
    LUMPSUM_CANCEL("15"),
    INSTALLMENT_CANCEL("18"),
    UNKNOWN(""),
    ;

    private final String dbCode;

    ApprovedType(String dbCode) {
        this.dbCode = dbCode;
    }

    public static ApprovedType of(String code) {
        for (ApprovedType ac : ApprovedType.values()) {
            if (ac.getDbCode().equalsIgnoreCase(code)) {
                return ac;
            }
        }
        return UNKNOWN;
    }
}
