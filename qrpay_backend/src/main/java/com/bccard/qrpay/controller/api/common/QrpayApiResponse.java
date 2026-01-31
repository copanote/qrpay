package com.bccard.qrpay.controller.api.common;

import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(description = "API 공통 응답")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QrpayApiResponse<T> {
    @Schema(description = "응답코드", example = "MP0000")
    private String code;

    @Schema(description = "응답코드 메시지")
    private String message;

    @Schema(description = "jwt인증 memberId")
    private String memberId;

    @Schema(description = "jwt인증 loginId")
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

    public static <T> QrpayApiResponse<T> ok(T data) {
        return new QrpayApiResponse<>(
                QrpayErrorCode.SUCCESS.getCode(), QrpayErrorCode.SUCCESS.getMessage(), "", "", data);
    }

    public static <T> QrpayApiResponse<T> ok(Member loginMember) {
        return new QrpayApiResponse<>(
                QrpayErrorCode.SUCCESS.getCode(),
                QrpayErrorCode.SUCCESS.getMessage(),
                loginMember.getMemberId(),
                loginMember.getLoginId(),
                null);
    }

    public static <T> QrpayApiResponse<T> ok() {
        return new QrpayApiResponse<>(QrpayErrorCode.SUCCESS.getCode(), QrpayErrorCode.SUCCESS.getMessage());
    }

    public static <T> QrpayApiResponse<T> error(String code, String message) {
        return new QrpayApiResponse<>(code, message);
    }
}
