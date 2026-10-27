package dev.storylab.story_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 4, max = 100)
        @Schema(
                description = "Unique username",
                example = "denise",
                minLength = 4,
                maxLength = 100

        )
        String username,

        @Schema(
                description = "User password",
                example = "MyPassword",
                minLength = 8,
                maxLength = 16

        )
        @NotBlank
        @Size(min = 8, max = 16)
        String password
) {
}
