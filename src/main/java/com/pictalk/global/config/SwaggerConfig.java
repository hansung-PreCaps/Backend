package com.pictalk.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .addServersItem(server);
    }

    public Info customOpenAPI() {
        return new Info()
                .title("Pic&Talk API 명세서")
                .version("1.0");
    }
}
