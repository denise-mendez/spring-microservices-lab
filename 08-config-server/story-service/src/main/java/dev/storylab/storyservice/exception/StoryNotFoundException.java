package dev.storylab.storyservice.exception;

public class StoryNotFoundException extends RuntimeException {
    public StoryNotFoundException(Long id) {
        super("Story with id " + id + " not found");
    }
}
