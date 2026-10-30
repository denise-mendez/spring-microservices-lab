package dev.storylab.story_api.dto;

import dev.storylab.story_api.entity.Role;

public record AuthResponse(
        String token,
        String type,
        String username,
        Role role
) {
}
