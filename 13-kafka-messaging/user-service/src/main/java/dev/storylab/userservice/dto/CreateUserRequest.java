package dev.storylab.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotNull
        Long accountId,
        @NotBlank
        String username,
        @NotBlank
        @Email
        String email
) {
}