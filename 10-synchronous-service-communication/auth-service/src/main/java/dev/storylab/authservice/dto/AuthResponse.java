package dev.storylab.authservice.dto;

import dev.storylab.authservice.security.SecurityRole;
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
                description = "authenticated user id.",
                example = "15"
        )
        Long userId,
        @Schema(
                description = "Authenticated username.",
                example = "denise"
        )
        String username,
        @Schema(
                description = "Role assigned to the authenticated user.",
                example = "USER"
        )
        SecurityRole role
) {
}
