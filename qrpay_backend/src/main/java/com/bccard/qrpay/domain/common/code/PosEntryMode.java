package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum PosEntryMode implements DatabaseCodeConvertable {
    BC_MPM("9400"),
    BC_CPM("1300"),
    EVONET_MPM("9420"),

    UNKNOWN("");
    private final String dbCode;

    PosEntryMode(String dbCode) {
        this.dbCode = dbCode;
    }

    public static PosEntryMode of(String code) {
        for (PosEntryMode pem : PosEntryMode.values()) {
            if (pem.getDbCode().equalsIgnoreCase(code)) {
                return pem;
            }
        }
        return UNKNOWN;
    }
}
