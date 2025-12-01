package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum MemberRole implements DatabaseCodeConvertable {
    MASTER("Y"),
    NORMAL("N"),
    UNDEFINED(""),
    ;

    MemberRole(String dbCode) {
        this.dbCode = dbCode;
    }

    private final String dbCode;

    public static MemberRole of(String code) {
        if ("Y".equalsIgnoreCase(code)) {
            return MASTER;
        }
        return NORMAL;
    }
}
