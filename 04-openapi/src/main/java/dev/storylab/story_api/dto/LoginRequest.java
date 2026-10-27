package dev.storylab.story_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(
                description = "Registered username",
                example = "denise"
        )
        @NotBlank
        String username,

        @Schema(
                description = "User password",
                example = "MyPassword"
        )
        @NotBlank
        String password
) {
}
