package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum FinancialInstitution implements DatabaseCodeConvertable {
    BCCARD("361"),
    SAMSUNGCARD("365"),
    SHINHANCARD("366"),
    HYUNDAICARD("367"),
    LOTTECARD("368"),
    NHCARD("371"),
    HANACARD("374"),
    KBCARD("381"),

    UNKNOWN(""),
    ;

    private final String dbCode;

    FinancialInstitution(String dbCode) {
        this.dbCode = dbCode;
    }

    public static FinancialInstitution of(String code) {
        for (FinancialInstitution fi : FinancialInstitution.values()) {
            if (fi.getDbCode().equalsIgnoreCase(code)) {
                return fi;
            }
        }
        return UNKNOWN;
    }
}
