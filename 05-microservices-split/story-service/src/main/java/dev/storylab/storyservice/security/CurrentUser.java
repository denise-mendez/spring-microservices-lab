package dev.storylab.storyservice.security;

public record CurrentUser(
        Long userId,
        SecurityRole role
) {
}
