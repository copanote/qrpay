package com.bccard.qrpay.utils;

import java.nio.charset.Charset;

public class MpmStringUtils {


    /**
     * 문자열을 지정된 바이트 길이(Byte Length)로 안전하게 자릅니다.
     * 멀티바이트 문자(예: 한글)가 중간에 잘리지 않도록 유효한 문자 경계까지만 포함합니다.
     *
     * @param str      원본 문자열
     * @param maxBytes 최대 허용 바이트 길이
     * @return 바이트 단위로 잘린 문자열
     */
    public static String safeSubstringByBytes(String str, int maxBytes, Charset charset) {
        if (str == null || str.isEmpty() || maxBytes <= 0) {
            return str;
        }

        byte[] bytes = str.getBytes(charset);

        // 1. 요청된 바이트 길이가 원본보다 길면 전체 반환
        if (bytes.length <= maxBytes) {
            return str;
        }

        // 2. 최대 바이트 길이로 일단 자름
        byte[] truncatedBytes = new byte[maxBytes];
        System.arraycopy(bytes, 0, truncatedBytes, 0, maxBytes);

        // 3. 바이트 배열을 문자열로 디코딩을 시도합니다.
        // 유효하지 않은 바이트 시퀀스가 포함되어 있으면 깨진 문자가 발생할 수 있습니다.
        String result = new String(truncatedBytes, charset);

        // 4. 깨진 문자가 발생했는지 확인하고 처리 (핵심)
        // 자른 문자열을 다시 바이트로 변환했을 때, 원래 자른 바이트 배열의 길이보다 짧다면
        // 이는 마지막 문자가 불완전하게 잘렸음을 의미합니다.
        if (result.getBytes(charset).length > truncatedBytes.length) {
            // 마지막 문자가 깨졌으므로, 그 문자를 제거하고 반환 (가장 일반적인 처리 방식)
            return result.substring(0, result.length() - 1);
        }

        return result;
    }
}
