package com.bccard.qrpay.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SystemErrorCode implements ErrorCode {

    SYSTEM_ERROR(500, "", "System오류"),
    CIPHER_HASH_ERROR(500, "", "Hash오류"),
    ;

    private final int status;
    private final String code;
    private final String message;
}
