package dev.storylab.story_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;


import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI storyApiOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Story API")
                                .version("1.0")
                                .description("REST API for creating, publishing and managing stories.")
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}