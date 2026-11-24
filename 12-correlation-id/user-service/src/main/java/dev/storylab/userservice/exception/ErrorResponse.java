package dev.storylab.userservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ErrorResponse",
        description = "Standard API error response"
)
public record ErrorResponse(
        @Schema(
                description = "HTTP status code",
                example = "404"
        )
        int status,
        @Schema(
                description = "Application error code",
                example = "USER_NOT_FOUND"
        )
        String error,
        @Schema(
                description = "Human-readable error description",
                example = "User with account id 10 not found"
        )
        String message
) {
}
