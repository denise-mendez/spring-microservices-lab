package dev.storylab.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                                .title("User Service API")
                                .version("1.0")
                                .description("User information API")
                );
    }
}