package com.bccard.qrpay.controller.api.common;

import com.bccard.qrpay.exception.code.QrpayErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QrpayApiResponse<T> {
    private String code;
    private String message;
    private T data;

    public static <T> QrpayApiResponse<T> ok(T data) {
        return new QrpayApiResponse<>(QrpayErrorCode.SUCCESS.getCode(), QrpayErrorCode.SUCCESS.getMessage(), data);
    }

    public static <T> QrpayApiResponse<T> ok() {
        return new QrpayApiResponse<>(QrpayErrorCode.SUCCESS.getCode(), QrpayErrorCode.SUCCESS.getMessage(), null);
    }

    public static <T> QrpayApiResponse<T> error(String code, String message) {
        return new QrpayApiResponse<>(code, message, null);
    }

}
