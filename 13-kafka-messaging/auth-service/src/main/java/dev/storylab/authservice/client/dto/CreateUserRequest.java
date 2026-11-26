package dev.storylab.authservice.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        Long accountId,
        String username,
        String email
) {
}