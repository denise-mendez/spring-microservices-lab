package dev.storylab.story_api.exception;

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
                example = "STORY_NOT_FOUND"
        )
        String error,
        @Schema(
                description = "Human-readable error description",
                example = "Story with id 999 was not found"
        )
        String message
) {
}
