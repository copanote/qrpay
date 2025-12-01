package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum MerchantRegister implements DatabaseCodeConvertable {
    MERCHANT("M"),
    ADMINISTRATOR("A"),
    UPI("U"),
    FI_MERCHANT("B"),
    UNKNOWN("");

    private final String dbCode;

    MerchantRegister(String dbCode) {
        this.dbCode = dbCode;
    }

    public static MerchantRegister of(String dbCode) {
        for (MerchantRegister mr : MerchantRegister.values()) {
            if (mr.getDbCode().equalsIgnoreCase(dbCode)) {
                return mr;
            }
        }
        return UNKNOWN;
    }
}
