package dev.storylab.authservice.dto;

import dev.storylab.authservice.security.SecurityRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "AccountResponse",
        description = "Public information about a registered user."
)

public record AccountResponse(
        @Schema(
                description = "Unique user identifier.",
                example = "1"
        )
        Long id,
        @Schema(
                description = "Unique username.",
                example = "denise"
        )
        String username,
        @Schema(
                description = "Role assigned to the user.",
                example = "USER"
        )
        SecurityRole role,
        @Schema(
                description = "Date and time when the user was created.",
                example = "2026-10-27T14:30:45"
        )
        LocalDateTime createdAt
) {
}
