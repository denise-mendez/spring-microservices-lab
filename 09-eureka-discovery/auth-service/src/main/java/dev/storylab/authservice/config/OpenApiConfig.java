package dev.storylab.authservice.config;

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
                                .title("Authentication Service API")
                                .version("1.0")
                                .description("Authentication and token issuance API")
                );
    }
}