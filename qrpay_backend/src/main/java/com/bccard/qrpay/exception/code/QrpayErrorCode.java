package com.bccard.qrpay.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QrpayErrorCode implements ErrorCode {

    //AUTH
    AUTHENTICATE_REQUIRED(401, "QE401", "인증필요"),
    UNMATCHED_AUTHENTICATE(403, "QE403", "인증정보와 요청정보가 일치하지 않습니다"),
    INVALID_CREDENTIAL(403, "E403", "ID 혹은 비밀번호가 올바르지 않습니다"),
    ACCOUNT_LOCKED_POLICY(403, "E_AUTH403", "비밀번호틀림 초과 횟수 정책 위반"),
    INVALID_AUTHORIZATION(403, "403", "접근권한이 없습니다"),
    NOT_FOUND_REFRESH_TOKEN(401, "E", "리프레시토큰없음(인증필요)"),
    INVALID_REFRESH_TOKEN(401, "E", "리프레시토큰인증실패(인증필요)"),

    //MEMBER
    MEMBER_NOT_FOUND(404, "", "회원정보를 찾을 수 없습니다."),
    MEMBER_CANCELED(409, "", "탈회한 사용자"),
    MEMBER_STATUS_CHANGE_NOT_ALLOWED(405, "", "탈회회원은 회원상태변경 불가)"),


    //MERCHANT
    MERCHANT_NOT_FOUND(404, "", "가맹점정보를 찾을 수 없습니다."),
    MERCHANT_CANCELED(400, "", "탈회한 가맹점"),


    //COMMON, SYSYEM
    SYSTEM_ERROR(500, "", "System오류"),
    CIPHER_HASH_ERROR(500, "", "Hash오류"),
    ;;;
    private final int status;
    private final String code;
    private final String message;
}
