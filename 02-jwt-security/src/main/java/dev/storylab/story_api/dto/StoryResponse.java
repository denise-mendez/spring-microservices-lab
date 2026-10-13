package dev.storylab.story_api.dto;

import dev.storylab.story_api.entity.StoryStatus;

import java.time.LocalDateTime;

public record StoryResponse(
        Long id,
        String title,
        String summary,
        String content,
        StoryStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}