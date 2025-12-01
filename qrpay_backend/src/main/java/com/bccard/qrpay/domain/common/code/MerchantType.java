package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum MerchantType implements DatabaseCodeConvertable {
    BASIC("01"),
    INDIVIDUAL("02"),
    P2P("03"),
    UPI("10"),
    ;

    private final String dbCode;

    MerchantType(String dbCode) {
        this.dbCode = dbCode;
    }

    public static MerchantType of(String code) {
        for (MerchantType mt : MerchantType.values()) {
            if (mt.getDbCode().equalsIgnoreCase(code)) {
                return mt;
            }
        }
        return BASIC;
    }
}
