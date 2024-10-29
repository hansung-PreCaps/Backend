package com.pictalk.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pic&Talk API 명세서",
                version = "1.0"
        )
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        return new OpenAPI()
                .components(components)
                .info(customOpenAPI())
                .addSecurityItem(securityRequirement)
                .components(components);

    }
    public io.swagger.v3.oas.models.info.Info customOpenAPI() {
        return new io.swagger.v3.oas.models.info.Info()
                .title("Pic&Talk API 명세서")
                .version("1.0");
    }
}
