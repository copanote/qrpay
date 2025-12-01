package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum MemberStatus implements DatabaseCodeConvertable {
    ACTIVE("00"),
    SUSPENDED("55"),
    CANCELLED("99"),

    UNKNOWN(""),
    ;

    private final String dbCode;

    MemberStatus(String dbCode) {
        this.dbCode = dbCode;
    }

    public static MemberStatus of(String code) {
        for (MemberStatus ms : MemberStatus.values()) {
            if (ms.getDbCode().equalsIgnoreCase(code)) {
                return ms;
            }
        }
        return UNKNOWN;
    }
}
