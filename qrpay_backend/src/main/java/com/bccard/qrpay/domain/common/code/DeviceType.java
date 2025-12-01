package com.bccard.qrpay.domain.common.code;

import lombok.Getter;

@Getter
public enum DeviceType implements DatabaseCodeConvertable {
    ANDROID("A"),
    IOS("I"),
    UNKNOWN(""),
    ;

    private final String dbCode;

    DeviceType(String dbCode) {
        this.dbCode = dbCode;
    }

    public static DeviceType of(String code) {

        for (DeviceType ms : DeviceType.values()) {
            if (ms.getDbCode().equalsIgnoreCase(code)) {
                return ms;
            }
        }

        return UNKNOWN;
    }
}
