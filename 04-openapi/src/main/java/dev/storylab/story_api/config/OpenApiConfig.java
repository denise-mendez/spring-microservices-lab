package dev.storylab.story_api.config;

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
                                .title("Story API")
                                .version("1.0")
                                .description("REST API for creating, publishing and managing stories.")
                );
    }
}