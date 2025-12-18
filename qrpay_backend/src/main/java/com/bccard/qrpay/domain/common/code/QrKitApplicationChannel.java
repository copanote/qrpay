package com.bccard.qrpay.domain.common.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum QrKitApplicationChannel implements DatabaseCodeConvertable {

    QRPAY_APP("1", "qrpay앱"),
    ADMIN_PAGE("2", "관리자페이지(DIPS)");

    private final String dbCode;
    private final String desc;

    public static QrKitApplicationChannel findByCode(String code) {
        for (QrKitApplicationChannel status : QrKitApplicationChannel.values()) {
            if (status.getDbCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }
}
