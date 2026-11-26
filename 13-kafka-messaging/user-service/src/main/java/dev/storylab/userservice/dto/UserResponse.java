package dev.storylab.userservice.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        Long accountId,
        String username,
        LocalDateTime createdAt
) {
}
