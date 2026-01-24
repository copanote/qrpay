package com.bccard.qrpay.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "dev"})
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";

        // 1. 보안 요구사항 설정 (모든 API에 적용)
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        // 2. 보안 스키마 정의 (Header에 어떻게 토큰을 넣을지 정의)
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components); // 중복된 .components(new Components())를 제거했습니다.
    }

    private Info apiInfo() {
        return new Info()
                .title("QRPAY API")
                .description("qrpay api")
                .version("1.0.0");
    }

}
