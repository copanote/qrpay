package com.bccard.qrpay.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretHex;
    private long accessTokenExpiration; // 15분
    private long refreshTokenExpiration; // 7일
}
