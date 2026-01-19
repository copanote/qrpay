package com.bccard.qrpay.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QrpayErrorCode implements ErrorCode {

    /*
    AU (Auth): 인증/인가 관련
    MB (Member): 회원 관련
    MC (Merchant): 가맹점 관련
    TX (Transaction): 거래/결제 관련
    QR (QR): mpmqr/qrkit 관련
    CM (Common): 공통/시스템 관련
     */

    SUCCESS(200, "MP0000", "Success"),

    // AUTH: AU
    AUTHENTICATE_REQUIRED(401, "AU0001", "인증필요"),
    AUTHENTICATE_REQUIRED_ARGUMENTHANDLER(401, "AU0002", "인증필요(ARGUMENT_HANDLER)"),
    UNMATCHED_AUTHENTICATE(403, "AU0003", "인증정보와 요청정보가 일치하지 않습니다"),
    INVALID_CREDENTIAL(403, "AU0004", "비밀번호가 올바르지 않습니다. 오류 횟수 5회 이상 시, 접속이 제한됩니다."),
    ACCOUNT_LOCKED_POLICY(403, "AU0005", "비밀번호 오류 횟수 5회 이상으로 접속이 제한됩니다. 비밀번호를 재성정해주세요."),
    INVALID_AUTHORIZATION(403, "AU0006", "접근권한이 없습니다"),
    NOT_FOUND_REFRESH_TOKEN(401, "AU0007", "리프레시토큰없음(인증필요)"),
    INVALID_REFRESH_TOKEN(401, "AU0008", "리프레시토큰인증실패(인증필요)"),

    // MEMBER:  MB
    MEMBER_NOT_FOUND(404, "MB0001", "회원정보를 찾을 수 없습니다."),
    MEMBER_CANCELED(409, "MB0002", "탈회한 사용자"),
    MEMBER_SUSPENDED(409, "MB0003", "일시정지(사용중지) 사용자"),
    MEMBER_STATUS_CHANGE_NOT_ALLOWED(405, "MB0004", "탈회회원은 회원상태변경 불가)"),
    MEMBER_CANCEL_REQUESTOR_INVALID_AUTHORIZATION(403, "MB0005", "해지요청자(가맹점주)와 사용자의 가맹점 불일치"),
    MEMBER_CANCEL_NOT_EMPLOYEE(405, "MB0006", "일반사용자가 아닙니다"),
    DISALLOW_CURRENT_PASSWORD_REUSE(409, "MB0007", "비밀번호 재사용 금지"),
    PASSWORD_CONFIRM_MISMATCH(409, "MB0008", "확인비밀번호 불일치"),
    PASSWORD_POLICY_VIOLATION(409, "MB0009", "비밀번호 정책위반.(6자리 숫자, 동일/연속 3자리 이상 입력불가)"),
    LOGIN_ID_CONFLICT(409, "MB0010", "동일ID 존재"),
    LOGIN_ID_POLICY_VIOLATION(409, "MB0011", "ID 정책위반(4~12자리의 영문/숫자/영문+숫자 조합)"),

    // MERCHANT: MC
    MERCHANT_NOT_FOUND(404, "MC0001", "가맹점정보를 찾을 수 없습니다."),
    MERCHANT_CANCELED(409, "MC0002", "탈회한 가맹점"),
    MERCHANT_IS_NOT_ACTIVE(409, "MC0003", "활성화 가맹점 아님"),
    MERCHANT_NAME_LENGTH_POLICY_VIOLATION(409, "MC0004", "가맹점이름은 14자리 이하로 변경 가능합니다."),

    // TRANSACTION: TX
    INVALID_SEARCH_CONDITION(400, "TX0001", "조회 조건이 잘 못 되었습니다."),

    // QR
    QRKIT_MAX_LIMIT_EXCEEDED(405, "QR0001", "QR KIT는 최대 3개까지 신청 가능합니다."),
    NOT_SUPPORT_PIM(405, "QR0002", "지원하지 않는 MPMQR PIM(STATIC, DYNAMIC)"),

    // External: Nice NC
    VERIFICATION_ALREADY_CONSUMED(403, "NC0001", "이미 사용완료된 인증정보"),

    // COMMON CM
    LOG_NOT_FOUND(404, "CM0001", "QRPAYLOG를 찾을 수 없습니다."),
    INVALID_PARAMETER(400, "CM0002", "Invalid Parameters"),
    CIPHER_HASH_ERROR(500, "CM0003", "Hash오류"),
    SYSTEM_ERROR(500, "CM9999", "System오류"),
    ;

    private final int status;
    private final String code;
    private final String message;
}
