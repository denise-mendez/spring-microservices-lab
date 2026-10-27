package dev.storylab.story_api.dto;

import dev.storylab.story_api.entity.StoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateStoryRequest(
        @Schema(
                description = "Story title",
                example = "The Midnight Train",
                maxLength = 150

        )
        @Size(max = 150)
        String title,

        @Schema(
                description = "Short description of the Story",
                example = "A mystery story",
                maxLength = 500

        )
        @Size(max = 500)
        String summary,

        @Schema(
                description = "Story content",
                example = "The station keeper was about to..."

        )
        @NotBlank
        String content,

        @Schema(
                description = "Story publication status",
                example = "PUBLISHED"

        )
        @NotNull
        StoryStatus status
) {
}
