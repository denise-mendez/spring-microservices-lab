package dev.storylab.storyservice.service;

import dev.storylab.storyservice.dto.CreateStoryRequest;
import dev.storylab.storyservice.dto.StoryResponse;
import dev.storylab.storyservice.dto.UpdateStoryRequest;
import dev.storylab.storyservice.entity.Story;
import dev.storylab.storyservice.entity.StoryStatus;
import dev.storylab.storyservice.exception.StoryNotFoundException;
import dev.storylab.storyservice.repository.StoryRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public List<StoryResponse> getAllStories() {
        return getPublishedStories();
    }

    public StoryResponse getStoryById(Long id) {
        return storyRepository
                .findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new StoryNotFoundException(id));
    }

    public List<StoryResponse> getStoriesByStatus(StoryStatus status) {
        return storyRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Story findStoryById(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(
                        () -> new StoryNotFoundException(id)
                );
    }

    private StoryResponse toResponse(Story story) {
        return new StoryResponse(
                story.getId(),
                story.getTitle(),
                story.getSummary(),
                story.getContent(),
                story.getStatus(),
                story.getAuthorId(),
                story.getCreatedAt(),
                story.getUpdatedAt()
        );
    }

    private List<StoryResponse> getPublishedStories() {
        return storyRepository
                .findByStatus(StoryStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
