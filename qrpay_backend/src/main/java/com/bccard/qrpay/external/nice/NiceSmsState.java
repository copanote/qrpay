package com.bccard.qrpay.external.nice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NiceSmsState {
    REQUEST_PROGRESS("REQUEST", "Nice 요청암호화데이터 생성완료"),
    CALLBACK_RECEIVED("CALLBACK", "Nice 암호화데이터 콜백수신 완료"),
    CONFIRM_COMPLETE("CONFIRM", "Nice 인증 확인 완료");

    private final String id;
    private final String desc;

    public static NiceSmsState findById(String id) {
        for (NiceSmsState state : NiceSmsState.values()) {
            if (state.getId().equals(id)) {
                return state;
            }
        }
        return null;
    }
}
