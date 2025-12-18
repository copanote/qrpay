package com.bccard.qrpay.utils;

import java.util.regex.Pattern;

public class IdValidator {

    /**
     * ID가 다음 규칙을 모두 만족하는지 검증합니다.
     * 1. 길이: 4자 이상 12자 이하
     * 2. 허용 문자: 영문(대소문자)과 숫자만 포함
     * 3. 조합: 영문만, 숫자만, 영문+숫자 조합 모두 허용
     *
     * @param id 검증할 ID 문자열
     * @return 유효성 검증 성공 시 true, 실패 시 false
     */
    public static boolean isValid(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }

        // 정규식 설명:
        // ^                   : 문자열의 시작
        // [a-zA-Z0-9]         : 영문 대소문자 (a-z, A-Z) 또는 숫자 (0-9) 중 하나
        // {4,12}              : 앞의 패턴이 최소 4번, 최대 12번 반복됨 (길이 제한)
        // $                   : 문자열의 끝
        final String ID_REGEX = "^[a-zA-Z0-9]{4,12}$";

        // Pattern.matches()는 전체 문자열에 대해 패턴 일치 여부를 검사합니다.
        if (!Pattern.matches(ID_REGEX, id)) {
            // log.warn("ID 형식 또는 길이가 규칙을 위반했습니다: {}", id);
            return false;
        }

        if (id.startsWith("system")) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        System.out.println("--- ID 유효성 검증 테스트 ---");

        // ✅ 성공 사례 (4~12자, 영문/숫자 조합 모두 포함)
        System.out.println("user123 (성공/영문+숫자): " + isValid("user123")); // true
        System.out.println("abcd (성공/영문만): " + isValid("abcd")); // true
        System.out.println("123456 (성공/숫자만): " + isValid("123456")); // true
        System.out.println("ABCDEFGHIJKL (성공/12자): " + isValid("ABCDEFGHIJKL")); // true

        // ❌ 실패 사례 (규칙 위반)
        System.out.println("usr (실패/3자): " + isValid("usr")); // false
        System.out.println("user_123 (실패/특수문자 포함): " + isValid("user_123")); // false
        System.out.println("user@123 (실패/특수문자 포함): " + isValid("user@123")); // false
        System.out.println("TooLongID1234 (실패/13자): " + isValid("TooLongID1234")); // false
        System.out.println("빈 문자열 (실패): " + isValid("")); // false
    }
}