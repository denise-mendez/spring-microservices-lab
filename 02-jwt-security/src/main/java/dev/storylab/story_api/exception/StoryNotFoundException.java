package dev.storylab.story_api.exception;

public class StoryNotFoundException extends RuntimeException {
    public StoryNotFoundException(Long id) {
        super("Story with id " + id + " not found");
    }
}
