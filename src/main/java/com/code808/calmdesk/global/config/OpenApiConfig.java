package com.code808.calmdesk.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CalmDesk API Documentation")
                        .description("### CalmDesk 프로젝트 API 명세서\n\n"
                                + "1. **로그인**: `/api/auth/login` 호출하여 `accessToken`을 발급받습니다.\n"
                                + "2. **인증 주입**: 상단의 **Authorize** 버튼을 클릭합니다.\n"
                                + "3. **값 입력**: 발급받은 `accessToken`을 입력합니다. (Bearer 접두어 제외)\n"
                                + "4. **테스트**: 이제 권한이 필요한 API를 호출할 수 있습니다.")
                        .version("v0.0.1"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
