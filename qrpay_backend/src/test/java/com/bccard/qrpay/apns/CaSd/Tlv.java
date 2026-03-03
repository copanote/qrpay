package com.bccard.qrpay.apns.CaSd;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public class Tlv {
    private byte[] tag;
    private byte[] length;
    private byte[] value;

    public String getHexValue() {
        return toHex(value);
    }

    public byte[] getAll() {
        // 세 배열의 길이를 모두 합산
        int totalLength = (tag != null ? tag.length : 0) +
                (length != null ? length.length : 0) +
                (value != null ? value.length : 0);

        ByteBuffer buffer = ByteBuffer.allocate(totalLength);

        if (tag != null) buffer.put(tag);
        if (length != null) buffer.put(length);
        if (value != null) buffer.put(value);

        return buffer.array();
    }

    private String toHex(byte[] bytes) {
        if (bytes == null) return "";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Tlv{" +
                "tag=" + toHex(tag) +
                ", length=" + toHex(length) +
                ", value=" + toHex(value) +
                '}';
    }
}
