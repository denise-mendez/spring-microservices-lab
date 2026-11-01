package dev.storylab.storyservice.config;

import io.swagger.v3.oas.models.OpenAPI;


import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI storyApiOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Story Service API")
                                .version("1.0")
                                .description("Story service responsible for creating, publishing and managing stories.")
                );
    }
}