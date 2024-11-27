package com.pictalk.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    @Value("${server.domain}")
    private String domain;


    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();
        server.setUrl(domain);

        return new OpenAPI()
                .components(new Components())
                .info(customOpenAPI())
                .addServersItem(server)
                .addSecurityItem(new SecurityRequirement().addList("JWT"));
    }

    public Info customOpenAPI() {
        return new Info()
                .title("Pic&Talk API 명세서")
                .version("1.0");
    }

    public SwaggerConfig(MappingJackson2HttpMessageConverter converter) {
        /**
         * content-type이 null인 경우 spring에서는 application/octet-stream으로 인식하여 처리하게 됨
         * 하지만 application/octet-stream에 대한 기본 컨버터가 없기 때문에 MappingJackson2HttpMessageConverter에 application/octet-stream을 추가 함
         */
        List<MediaType> supportMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportMediaTypes);
    }
}
