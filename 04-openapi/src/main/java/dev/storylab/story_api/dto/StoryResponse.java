package dev.storylab.story_api.dto;

import dev.storylab.story_api.entity.StoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record StoryResponse(
        @Schema(example = "1")
        Long id,
        @Schema(example = "The Midnight Train")
        String title,
        @Schema(example = "A short mystery story.")
        String summary,
        @Schema(example = "The station keeper was about to...")
        String content,
        @Schema(example = "DRAFT")
        StoryStatus status,
        @Schema(example = "denise")
        String author,
        @Schema(
                description = "Date and time when the Story was created.",
                example = "2026-10-27T14:30:45"
        )
        LocalDateTime createdAt,
        @Schema(
                description = "Date and time when the Story was last updated.",
                example = "2026-10-27T14:30:45"
        )
        LocalDateTime updatedAt
) {
}