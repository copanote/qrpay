package com.bccard.qrpay.controller.api.common;

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
        return new QrpayApiResponse<>("2000", "SUCCESS", data);
    }

    public static <T> QrpayApiResponse<T> error(String code, String message) {
        return new QrpayApiResponse<>(code, message, null);
    }

}
