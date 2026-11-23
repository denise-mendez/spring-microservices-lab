package dev.storylab.authservice.dto;

import dev.storylab.authservice.security.SecurityRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
        @NotNull
        SecurityRole role
) {
}
