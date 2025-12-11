package com.bccard.qrpay.auth.service;

import com.bccard.qrpay.utils.security.HashCipher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class CustomPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return HashCipher.sha256EncodedBase64(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return HashCipher.sha256EncodedBase64(rawPassword.toString()).equals(encodedPassword);
    }
}
