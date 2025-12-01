package com.bccard.qrpay.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_CREDENTIAL(403, "403", "ID 혹은 비밀번호가 올바르지 않습니다"),
    ACCOUNT_LOCKED_POLICY(403, "E_AUTH403", "비밀번호틀림 초과 횟수 정책 위반"),

    INVALID_AUTHORIZATION(403, "403", "접근권한이 없습니다"),

    NOT_FOUND_REFRESH_TOKEN(400, "E", "Not found refreshToken"),
    INVALID_REFRESH_TOKEN(403, "E", "Invalid RefreshToken "),
    ;
    private final int status;
    private final String code;
    private final String message;
}
