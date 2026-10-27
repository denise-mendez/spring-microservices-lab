package dev.storylab.story_api.dto;

import dev.storylab.story_api.entity.StoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateStoryRequest(
        @Size(max = 150)
        String title,
        @Size(max = 500)
        String summary,
        @NotBlank
        String content,
        @NotNull
        StoryStatus status
) {
}
