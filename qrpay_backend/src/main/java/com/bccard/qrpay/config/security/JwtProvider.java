package com.bccard.qrpay.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.sql.Date;
import java.time.Instant;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {

    @Getter
    private final JwtProperties jwtProperties;

    //    public static SecretKey key = Jwts.SIG.HS256.key().build();
    private SecretKey key;

    public JwtProvider(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(Hex.decode(jwtProperties.getSecretHex()));
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(String userId, String role) {
        return generateToken(userId, role, jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String userId) {
        return generateToken(userId, "", jwtProperties.getRefreshTokenExpiration());
    }

    private String generateToken(String userId, String roles, long expirationTime) {

        Instant now = Instant.now();
        Claims claims = Jwts.claims().add("roles", roles).build();

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(userId)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationTime)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> validateAndParse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    public String validateAndGetSubject(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
