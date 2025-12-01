package com.bccard.qrpay.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtProviderTest {

    @Autowired
    JwtProvider jwtProvider;

    @Test
    void test_at() {

        String jwt = jwtProvider.generateToken("1", "manager");

        System.out.println(jwt);

        Jws<Claims> claimsJws = jwtProvider.validateAndParse(jwt);
        System.out.println(claimsJws.getDigest());
        System.out.println(claimsJws.getHeader());
        System.out.println(claimsJws.getPayload());


        System.out.println(jwtProvider.validateAndParse(jwt));


    }
}
