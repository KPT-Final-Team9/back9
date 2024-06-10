package com.core.back9.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "파이널 프로젝트 9조 api 명세서",
                description = "<h3>호실 평가 데이터 대시보드 구현 프로젝트</h3> \n\n"
                              + "<b>[StoryBook](https://www.chromatic.com/builds?appId=66421fd64f35d30603e16002)</b> \n\n"
                              + "<b>[FrontEnd Deploy Link](https://front-alpha-five.vercel.app/dashboard)</b> \n\n"
                              + "<b>[Team Notion](https://www.notion.so/9-71027abc03c24746aeb6c5cb2e7bac29)</b> \n\n"
                              + "<b>[Team Figma](https://www.figma.com/design/aZWKlkBTP2eOY6DfgGUkXm/%EC%98%A4%ED%94%BC%EC%8A%A49%EC%A1%B0%EB%8C%80_Figma?node-id=825-1208&t=VoHJV0woufk9u02Q-0)</b>",
                version = "v1"
        )
)
@RequiredArgsConstructor
public class SwaggerConfig {

    private final Environment env;

    String root = "com.core.back9.controller";
    String actuator = "com.core.back9.common.actuator";
    String[] paths = {
            root,
            actuator
    };

    @Bean
    public GroupedOpenApi getEntireApi() {
        return GroupedOpenApi.builder()
                .group("entire")
                .packagesToScan(paths)
                .build();
    }

    @Bean
    public OpenAPI apiInfo() {
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .scheme("Bearer")
                .in(SecurityScheme.In.HEADER) // 헤더에 위치
                .name("Authorization"); // 이름은 Authorization

        List<Tag> tagList = getTagList();

        String[] activeProfiles = env.getActiveProfiles();
        Map<String, String> urlSetting = getServerUrlMap(activeProfiles);

        Map.Entry<String, String> entry = urlSetting.entrySet().iterator().next();
        String url = entry.getKey();
        String description = entry.getValue();

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        return new OpenAPI()
                .tags(tagList)
                .servers(List.of(
                        new Server().url(url).description(description)
                ))
                .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement);
    }

    private Map<String, String> getServerUrlMap(String[] activeProfiles) {
        Map<String, String> urlSetting = new HashMap<>();

        if (List.of(activeProfiles).contains("dev")) {
            urlSetting.put("https://officedev.site", "dev server");
        } else if (List.of(activeProfiles).contains("local")) {
            urlSetting.put("http://localhost:8080", "local server");
        } else {
            urlSetting.put("http://localhost:8080", "test server");
        }
        return urlSetting;
    }

    private List<Tag> getTagList() {
        return List.of(
                new Tag().name("member-public-controller").description("<b>[공통]</b> 회원가입 & 로그인 API"),
                new Tag().name("building-controller").description("<b>[관리자(ADMIN), 공통]</b> 빌딩 API"),
                new Tag().name("owner-building-controller").description("<b>[관리자(ADMIN), 공통]</b> 빌딩 API"),
                new Tag().name("room-controller").description("<b>[관리자(ADMIN)]</b> 호실 생성 & 수정 & 삭제 API"),
                new Tag().name("owner-score-controller").description("<b>[소유자(OWNER)]</b> 호실 평가 발행 & 설정 API"),
                new Tag().name("owner-room-controller").description("<b>[소유자(OWNER)]</b> 호실 평가 조회 & 북마크 API"),
                new Tag().name("contract-controller").description("<b>[OWNER]</b> 계약 & 주요 통계 정보 조회 API"),
                new Tag().name("tenant-controller").description("<b>[관리자(ADMIN)]</b> 입주사 API"),
                new Tag().name("tenant-public-controller").description("<b>[공통]</b> 입주사 정보 조회 API"),
                new Tag().name("user-score-controller").description("<b>[입주자(USER)]</b> 입주자 호실 평가 API")
        );
    }

}
