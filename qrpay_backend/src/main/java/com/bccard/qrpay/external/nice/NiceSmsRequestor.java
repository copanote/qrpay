package com.bccard.qrpay.external.nice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NiceSmsRequestor {
    SIGNUP("signup", "auth/sign-up/signup-terms-01", "auth/sign-up/signup-merchant-03", ""),
    FIND_ID("find_id", "auth/login", "auth/find-id/findid-merchant-02", "" + ""),
    PASSWORD_RESET("password_reset", "auth/find-pw/findpw-checkId-01", "auth/find-pw/findpw-change-03", ""),
    DEVICE_CHANGED("device_changed", "auth/login", "mpmqr/main-mpmqr", "");

    //    private static final String prefix = "/pages/auth";
    private final String id;
    private final String prevView;
    private final String nextView;
    private final String failView;

    public static NiceSmsRequestor findById(String id) {
        for (NiceSmsRequestor value : NiceSmsRequestor.values()) {
            if (value.getId().equalsIgnoreCase(id)) {
                return value;
            }
        }
        return null;
    }
}
