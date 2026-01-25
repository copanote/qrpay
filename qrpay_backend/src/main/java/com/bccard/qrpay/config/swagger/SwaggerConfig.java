package com.bccard.qrpay.config.swagger;

import com.bccard.qrpay.config.web.argumentresolver.LoginMember;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "dev"})
public class SwaggerConfig {

    private final String JWT_NAME = "JWT";

    static {
        // LoginMember 어노테이션을 가진 파라미터는 모든 API 문서에서 제외처리
        SpringDocUtils.getConfig().addAnnotationsToIgnore(LoginMember.class);
    }

    // 1. 공개 API 그룹 (인증 불필요)
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("01. 공개 API(인증 불필요)")
                .pathsToMatch("/qrpay/api/open/**", "/auth/**", "/external/**") // 인증이 필요 없는 경로 패턴
                .build();
    }

    // 2. 보안 API 그룹 (JWT 인증 필요)
    @Bean
    public GroupedOpenApi securedApi() {
        return GroupedOpenApi.builder()
                .group("02. 보안 API (JWT 인증 필요)")
                .pathsToMatch("/qrpay/api/v1/**") // 전체 API 중
                .pathsToExclude("/qrpay/api/open/**", "/auth/**") // 공개 경로는 제외
                // 이 그룹에만 자물쇠(SecurityRequirement) 적용
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(new SecurityRequirement().addList(JWT_NAME)))
                .build();
    }

    // 3. 전역 설정 (모든 그룹에 공통 적용될 메타데이터)
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(new Components()
                        .addSecuritySchemes(JWT_NAME, new SecurityScheme()
                                .name(JWT_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        ));
    }

    private Info apiInfo() {
        return new Info()
                .title("QRPAY API")
                .description("QRPAY API 문서입니다.")
                .version("1.0.0");
    }
}