package com.bccard.qrpay.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    /**
     * 비밀번호가 다음 규칙을 모두 만족하는지 검증합니다.
     * 1. 정확히 6자리 숫자 형태
     * 2. 동일한 숫자 3회 이상 연속 사용 금지 (예: 111, 333333 -> 불가능)
     * 3. 연속된 숫자 3회 이상 사용 금지 (예: 123, 765, 901 -> 불가능)
     * * @param password 검증할 비밀번호 문자열
     * @return 유효성 검증 성공 시 true, 실패 시 false
     */
    public static boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // 1. 길이 및 숫자 형태 검증 (정확히 6자리 숫자인지)
        if (!password.matches("^\\d{6}$")) {
            // log.warn("비밀번호 길이 또는 형식이 잘못되었습니다: {}", password);
            return false;
        }

        // 2. 동일한 숫자 3회 이상 연속 사용 금지 검증 (예: 111, 222, ...)
        if (hasConsecutiveIdenticalDigits(password)) {
            // log.warn("동일한 숫자 3회 이상 연속 사용 금지: {}", password);
            return false;
        }

        // 3. 연속된 숫자 3회 이상 사용 금지 검증 (예: 123, 765, ...)
        if (hasConsecutiveSequentialDigits(password)) {
            // log.warn("연속된 숫자 3회 이상 사용 금지: {}", password);
            return false;
        }

        return true;
    }

    /**
     * 동일한 숫자 3회 이상 연속 사용 여부를 검사합니다.
     * 정규식: (\d)\1{2,} -> 임의의 숫자(\d) 뒤에 그 숫자가 2회 이상 반복되는 패턴 (총 3회 이상)
     */
    private static boolean hasConsecutiveIdenticalDigits(String password) {
        Pattern pattern = Pattern.compile("(\\d)\\1{2,}");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

    /**
     * 연속된 숫자 3회 이상 사용 여부를 검사합니다. (오름차순/내림차순 모두 포함)
     * 최소 3자리 이상이 필요하므로, 6자리 비밀번호에서는 총 4개의 조합만 확인하면 됩니다.
     */
    private static boolean hasConsecutiveSequentialDigits(String password) {
        for (int i = 0; i <= password.length() - 3; i++) {
            int d1 = Character.getNumericValue(password.charAt(i));
            int d2 = Character.getNumericValue(password.charAt(i + 1));
            int d3 = Character.getNumericValue(password.charAt(i + 2));

            // 오름차순 검사 (예: 123, 456)
            if (d2 == d1 + 1 && d3 == d2 + 1) {
                return true;
            }

            // 내림차순 검사 (예: 321, 654)
            if (d2 == d1 - 1 && d3 == d2 - 1) {
                return true;
            }
        }
        return false;
    }

//    public static void main(String[] args) {
//        System.out.println("--- 6자리 숫자 검증 테스트 ---");
//        System.out.println("123456 (성공): " + isValid("123456")); // true
//        System.out.println("12345 (실패/길이): " + isValid("12345")); // false
//        System.out.println("ABCDEF (실패/문자): " + isValid("ABCDEF")); // false
//        System.out.println("1234567 (실패/길이): " + isValid("1234567")); // false
//
//        System.out.println("\n--- 동일 숫자 3회 연속 검증 테스트 ---");
//        System.out.println("111234 (실패): " + isValid("111234")); // false (111)
//        System.out.println("123444 (실패): " + isValid("123444")); // false (444)
//        System.out.println("122345 (성공): " + isValid("122345")); // true (22)
//
//        System.out.println("\n--- 연속 숫자 3회 연속 검증 테스트 ---");
//        System.out.println("123456 (실패): " + isValid("123456")); // false (123)
//        System.out.println("987654 (실패): " + isValid("987654")); // false (987)
//        System.out.println("121456 (성공): " + isValid("121456")); // true
//        System.out.println("901234 (실패): " + isValid("901234")); // false (012)
//        System.out.println("321456 (실패): " + isValid("321456")); // false (321)
//    }
}