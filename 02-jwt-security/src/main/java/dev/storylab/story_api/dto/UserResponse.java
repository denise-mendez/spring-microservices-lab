package dev.storylab.story_api.dto;

import java.time.LocalDateTime;
import dev.storylab.story_api.entity.Role;

public record UserResponse(
        Long id,
        String username,
        Role role,
        LocalDateTime createdAt
) {
}
