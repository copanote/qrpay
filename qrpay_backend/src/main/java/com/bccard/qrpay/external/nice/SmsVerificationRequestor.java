package com.bccard.qrpay.external.nice;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SmsVerificationRequestor {

    SIGNUP("signup", "", ""),
    FIND_ID("find_id", "", ""),
    PASSWORD_RESET("password_reset", "", ""),
    ;

    private final String id;
    private final String prevPage;
    private final String nextPage;

    public static SmsVerificationRequestor findById(String id) {
        for (SmsVerificationRequestor value : SmsVerificationRequestor.values()) {
            if (value.getId().equalsIgnoreCase(id)) {
                return value;
            }
        }
        return null;
    }

}
