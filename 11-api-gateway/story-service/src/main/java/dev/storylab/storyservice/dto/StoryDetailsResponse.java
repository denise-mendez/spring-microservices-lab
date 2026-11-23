package dev.storylab.storyservice.dto;

import dev.storylab.storyservice.client.dto.AuthorSummary;
import dev.storylab.storyservice.entity.StoryStatus;

import java.time.LocalDateTime;

public record StoryDetailsResponse(
        Long id,
        String title,
        String summary,
        String content,
        StoryStatus status,
        AuthorSummary author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
