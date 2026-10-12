package dev.storylab.story_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(StoryNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleStoryNotFound(StoryNotFoundException snfe) {
    return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "STORY_NOT_FOUND",
            snfe.getMessage()
    );
  }
}
