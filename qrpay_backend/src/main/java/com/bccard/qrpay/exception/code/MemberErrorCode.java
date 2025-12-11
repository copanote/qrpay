package com.bccard.qrpay.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(400, "", "회원정보를 찾을 수 없습니다."),
    MEMBER_CANCELED(400, "", "탈회한 사용자"),

    ;

    private final int status;
    private final String code;
    private final String message;

}
