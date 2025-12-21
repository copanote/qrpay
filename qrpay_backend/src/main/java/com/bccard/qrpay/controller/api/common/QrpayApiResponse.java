package com.bccard.qrpay.controller.api.common;

import com.bccard.qrpay.domain.member.Member;
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
    private String memberId;
    private String loginId;
    private T data;

    public QrpayApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }


    public static <T> QrpayApiResponse<T> ok(Member loginMember, T data) {
        return new QrpayApiResponse<>(
                QrpayErrorCode.SUCCESS.getCode(),
                QrpayErrorCode.SUCCESS.getMessage(),
                loginMember.getMemberId(),
                loginMember.getLoginId(),
                data);
    }

    public static <T> QrpayApiResponse<T> ok(Member loginMember) {
        return new QrpayApiResponse<>(
                QrpayErrorCode.SUCCESS.getCode(),
                QrpayErrorCode.SUCCESS.getMessage(),
                loginMember.getMemberId(),
                loginMember.getLoginId(),
                null);
    }

    public static <T> QrpayApiResponse<T> error(String code, String message) {
        return new QrpayApiResponse<>(code, message);
    }

}
