package dev.storylab.story_api.dto;

import dev.storylab.story_api.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(
                description = "JWT used to authenticate subsequent requests.",
                example = "eyJhbG..."
        )
        String token,
        @Schema(
                description = "Authentication scheme used with the token.",
                example = "Bearer"
        )
        String type,
        @Schema(
                description = "Authenticated username.",
                example = "denise"
        )
        String username,
        @Schema(
                description = "Role assigned to the authenticated user.",
                example = "USER"
        )
        Role role
) {
}
