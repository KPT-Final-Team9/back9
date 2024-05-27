package com.core.back9.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    String root = "com.core.back9.controller";
    String[] paths = {
            root
    };

    @Bean
    public GroupedOpenApi getEntireApi() {
        return GroupedOpenApi.builder()
                .group("Entire")
                .packagesToScan(paths)
                .build();
    }

    @Bean
    public OpenAPI apiKey() {
        SecurityScheme apiKey = new SecurityScheme() // API Key 정의
                .type(SecurityScheme.Type.APIKEY) //API Key정의
                .in(SecurityScheme.In.HEADER) // 헤더에 위치
                .name("Authorization"); // 이름은 Authorization

        SecurityRequirement securityRequirement = new SecurityRequirement() // 보안 요구사항 정의
                .addList("Bearer Token"); // Bearer Token 보안 요구사항 추가(모달창에 보임)

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement);
    }

}
