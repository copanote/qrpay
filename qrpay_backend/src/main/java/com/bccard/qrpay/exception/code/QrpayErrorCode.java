package com.bccard.qrpay.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QrpayErrorCode implements ErrorCode {

    SUCCESS(200, "MP0000", "Success"),

    //AUTH
    AUTHENTICATE_REQUIRED(401, "AE401", "인증필요"),
    AUTHENTICATE_REQUIRED_ARGUMENTHANDLER(401, "AE401", "인증필요(ARGUMENT_HANDLER)"),
    UNMATCHED_AUTHENTICATE(403, "AE403", "인증정보와 요청정보가 일치하지 않습니다"),
    INVALID_CREDENTIAL(403, "E403", "ID 혹은 비밀번호가 올바르지 않습니다"),
    ACCOUNT_LOCKED_POLICY(403, "E_AUTH403", "비밀번호틀림 초과 횟수 정책 위반"),
    INVALID_AUTHORIZATION(403, "403", "접근권한이 없습니다"),
    NOT_FOUND_REFRESH_TOKEN(401, "E", "리프레시토큰없음(인증필요)"),
    INVALID_REFRESH_TOKEN(401, "E", "리프레시토큰인증실패(인증필요)"),

    //MEMBER
    MEMBER_NOT_FOUND(404, "", "회원정보를 찾을 수 없습니다."),
    MEMBER_CANCELED(409, "", "탈회한 사용자"),
    MEMBER_STATUS_CHANGE_NOT_ALLOWED(405, "", "탈회회원은 회원상태변경 불가)"),

    DISALLOW_CURRENT_PASSWORD_REUSE(409, "ME409", "비밀번호 재사용 금지"),
    PASSWORD_CONFIRM_MISMATCH(409, "ME409", "확인비밀번호 불일치"),
    PASSWORD_POLICY_VIOLATION(409, "ME444", "비밀번호 정책위반.(6자리 숫자, 동일/연속 3자리 이상 입력불가)"),

    LOGIN_ID_CONFLICT(409, "ME4441", "동일ID 등록불가"),
    LOGIN_ID_POLICY_VIOLATION(409, "ME4441", "ID 정책위반(4~12자리의 영문/숫자/영문+숫자 조합)"),


    //MERCHANT
    MERCHANT_NOT_FOUND(404, "", "가맹점정보를 찾을 수 없습니다."),
    MERCHANT_CANCELED(409, "", "탈회한 가맹점"),

    //COMMON, SYSYEM
    SYSTEM_ERROR(500, "MP9999", "System오류"),
    CIPHER_HASH_ERROR(500, "", "Hash오류"),
    ;;;
    private final int status;
    private final String code;
    private final String message;
}
