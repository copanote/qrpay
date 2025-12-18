package com.bccard.qrpay.utils.security;

import com.bccard.qrpay.exception.QrpayCustomException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HashCipher {
    public static byte[] sha256(String data) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new QrpayCustomException(QrpayErrorCode.CIPHER_HASH_ERROR);
        }
        return md.digest();
    }

    public static String sha256EncodedBase64(String data) {
        byte[] b = sha256(data);
        return Base64.getEncoder().encodeToString(b);
    }


    public static byte[] sha512(String data) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new QrpayCustomException(QrpayErrorCode.CIPHER_HASH_ERROR);
        }
        return md.digest();
    }

    public static String sha512EncodedBase64(String data) {
        byte[] b = sha512(data);
        return Base64.getEncoder().encodeToString(b);
    }
}
