package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum AuthorizeType implements DatabaseCodeConvertable {
    AUTHORIZE("10"),
    CANCEL("90"),

    UNKNOWN(""), // default code value
    ;

    private final String dbCode;

    AuthorizeType(String dbCode) {
        this.dbCode = dbCode;
    }

    public static AuthorizeType of(String code) {

        for (AuthorizeType tkc : AuthorizeType.values()) {
            if (tkc.getDbCode().equalsIgnoreCase(code)) {
                return tkc;
            }
        }
        return UNKNOWN;
    }
}
