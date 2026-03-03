package com.bccard.qrpay.apns.CaSd;

import java.util.Arrays;

public class CasdParser {

    public static CasdCertificate parse(String hexData) {
        CasdCertificate cert = new CasdCertificate();
        byte[] raw = hexToBytes(hexData.replaceAll("\\s", ""));
        parseInternal(raw, 0, raw.length, cert, false);
        return cert;
    }

    private static void parseInternal(byte[] data, int offset, int limit, CasdCertificate cert, boolean isEcc) {
        int i = offset;
        while (i < limit) {
            // Tag, Length, Value 추출 로직 (동일)
            int tagStart = i;
            byte firstTagByte = data[i++];
            if ((firstTagByte & 0x1F) == 0x1F) i++;
            byte[] tagBytes = Arrays.copyOfRange(data, tagStart, i);
            String tagHex = bytesToHex(tagBytes).toUpperCase();

            int length = data[i++] & 0xFF;
            byte[] lenBytes = {(byte) length};
            byte[] valBytes = Arrays.copyOfRange(data, i, i + length);
            Tlv tlv = new Tlv(tagBytes, lenBytes, valBytes);

            // 데이터 저장
            if (isEcc) {
                cert.getEccFields().put(tagHex, tlv);
            } else {
                // 순서 보장을 위해 통합 맵에 먼저 저장
                cert.getAllFields().put(tagHex, tlv);

                // Constructed 태그(7F49)인지 확인 (Enum 참조 혹은 비트 체크)
                CasdCertificate.CasdField field = CasdCertificate.CasdField.fromTag(tagHex);
                if (field != null && field.isConstructed()) {
                    parseInternal(valBytes, 0, valBytes.length, cert, true);
                }
            }
            i += length;
        }
    }

    private static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }
}