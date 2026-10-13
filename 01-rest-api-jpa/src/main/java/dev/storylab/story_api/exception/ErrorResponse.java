package dev.storylab.story_api.exception;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}
