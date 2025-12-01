package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum RequesterCode implements DatabaseCodeConvertable {
    UPI("U", FinancialInstitution.UNKNOWN),
    BCCARD("B", FinancialInstitution.BCCARD),

    SAMSUNGCARD("Z", FinancialInstitution.SAMSUNGCARD),
    SHINHANCARD("S", FinancialInstitution.SHINHANCARD),
    HYUNDAICARD("Y", FinancialInstitution.HYUNDAICARD),
    LOTTECARD("L", FinancialInstitution.LOTTECARD),
    NHCARD("N", FinancialInstitution.NHCARD),
    HANACARD("H", FinancialInstitution.HANACARD),
    KBCARD("K", FinancialInstitution.KBCARD),

    UPI_INCOMING("A", FinancialInstitution.UNKNOWN),

    EVONET_INCOMING("E", FinancialInstitution.UNKNOWN),

    UNKNOWN("", FinancialInstitution.UNKNOWN),
    ;

    private final String dbCode;
    private final FinancialInstitution fi;

    RequesterCode(String dbCode, FinancialInstitution fi) {
        this.dbCode = dbCode;
        this.fi = fi;
    }

    public static RequesterCode of(String code) {

        for (RequesterCode sc : RequesterCode.values()) {
            if (sc.getDbCode().equalsIgnoreCase(code)) {
                return sc;
            }
        }
        return UNKNOWN;
    }

    public static RequesterCode of(FinancialInstitution fi) {

        for (RequesterCode sc : RequesterCode.values()) {
            if (sc.getFi() == fi) {
                return sc;
            }
        }
        return UNKNOWN;
    }
}
