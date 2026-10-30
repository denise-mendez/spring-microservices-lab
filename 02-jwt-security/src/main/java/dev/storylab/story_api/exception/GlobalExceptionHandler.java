package dev.storylab.story_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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

  @ExceptionHandler(UsernameAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleUsernameAlreadyExists(UsernameAlreadyExistsException uaee) {
    return new ErrorResponse(
      HttpStatus.CONFLICT.value(),
      "USERNAME_ALREADY_EXISTS",
      uaee.getMessage()
    );
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ErrorResponse handleMethodNotAllowed(HttpRequestMethodNotSupportedException hrmnse) {
    return new ErrorResponse(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            "METHOD_NOT_ALLOWED",
            hrmnse.getMessage()
    );
  }
}
