package dev.storylab.storyservice.service;

import dev.storylab.storyservice.dto.CreateStoryRequest;
import dev.storylab.storyservice.dto.StoryResponse;
import dev.storylab.storyservice.dto.UpdateStoryRequest;
import dev.storylab.storyservice.entity.Story;
import dev.storylab.storyservice.entity.StoryStatus;
import dev.storylab.storyservice.exception.StoryNotFoundException;
import dev.storylab.storyservice.repository.StoryRepository;
import dev.storylab.storyservice.security.CurrentUser;
import dev.storylab.storyservice.security.SecurityRole;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public StoryResponse create(CreateStoryRequest request, CurrentUser currentUser) {
        verifyCanPerformAction(currentUser);

        LocalDateTime now = LocalDateTime.now();
        Story story = new Story();


        story.setTitle(request.title());
        story.setSummary(request.summary());
        story.setContent(request.content());
        story.setStatus(request.status());
        story.setCreatedAt(now);
        story.setUpdatedAt(now);
        story.setAuthorId(currentUser.userId());

        Story savedStory = storyRepository.save(story);

        return toResponse(savedStory);
    }

    public List<StoryResponse> getAllStories(CurrentUser currentUser) {
        if (currentUser == null) {
            return getPublishedStories();
        }

        if (currentUser.role().equals(SecurityRole.ADMIN)) {
            return storyRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        if (currentUser.role().equals( SecurityRole.AUTHOR)) {
            List<Story> publishedStories = storyRepository.findByStatus(StoryStatus.PUBLISHED);
            List<Story> ownDrafts = storyRepository.findByStatusAndAuthorId(StoryStatus.DRAFT, currentUser.userId());

            return Stream.concat(publishedStories.stream(), ownDrafts.stream())
                    .map(this::toResponse)
                    .toList();
        }

        return getPublishedStories();
    }

    public StoryResponse getStoryById(Long id, CurrentUser currentUser) {
        Story story = findStoryById(id);

        if (story.getStatus() == StoryStatus.PUBLISHED) {
            return toResponse(story);
        }

        if (currentUser == null) {
            throw new StoryNotFoundException(id);
        }

        boolean admin = currentUser.role().equals(SecurityRole.ADMIN);
        boolean owner = Objects.equals(story.getAuthorId(), currentUser.userId());

        if (!admin && !owner) {
            throw new StoryNotFoundException(id);
        }

        return toResponse(story);
    }

    public List<StoryResponse> getStoriesByStatus(StoryStatus status, CurrentUser currentUser) {
        if(status.equals(StoryStatus.PUBLISHED)){
            return storyRepository.findByStatus(StoryStatus.PUBLISHED)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        if (currentUser == null) {
            return List.of();
        }

        if (currentUser.role().equals(SecurityRole.ADMIN)) {
            return storyRepository.findByStatus(StoryStatus.DRAFT)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        if (currentUser.role().equals(SecurityRole.AUTHOR)) {
            return storyRepository.findByStatusAndAuthorId(StoryStatus.DRAFT, currentUser.userId())
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return List.of();
    }

    public StoryResponse updateStory(Long id, UpdateStoryRequest request, CurrentUser currentUser) {
        Story story = findStoryById(id);

        verifyCanPerformAction(story, currentUser);

        story.setTitle(request.title());
        story.setSummary(request.summary());
        story.setContent(request.content());
        story.setStatus(request.status());
        story.setUpdatedAt(LocalDateTime.now());

        Story updatedStory = storyRepository.save(story);

        return toResponse(updatedStory);
    }

    public void deleteStory(Long id, CurrentUser currentUser) {
        Story story = findStoryById(id);

        verifyCanPerformAction(story, currentUser);

        storyRepository.delete(story);
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

    private void verifyCanPerformAction(@NonNull Story story, CurrentUser currentUser) {
        boolean isAdmin = currentUser.role().equals(SecurityRole.ADMIN);

        boolean isOwner = Objects.equals(story.getAuthorId(),
                currentUser.userId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to perform this action.");
        }
    }

    private void verifyCanPerformAction(CurrentUser currentUser) {
        boolean isAdmin = currentUser.role().equals(SecurityRole.ADMIN);
        boolean isAuthor = currentUser.role().equals(SecurityRole.AUTHOR);

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("You are not allowed to perform this action.");
        }
    }

    private List<StoryResponse> getPublishedStories() {
        return storyRepository
                .findByStatus(StoryStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
