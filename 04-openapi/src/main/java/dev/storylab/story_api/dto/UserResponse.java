package dev.storylab.story_api.dto;

import java.time.LocalDateTime;
import dev.storylab.story_api.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "UserResponse",
        description = "Public information about a registered user."
)
public record UserResponse(
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
        Role role,
        @Schema(
                description = "Date and time when the user was created.",
                example = "2026-10-27T14:30:45"
        )
        LocalDateTime createdAt
) {
}
