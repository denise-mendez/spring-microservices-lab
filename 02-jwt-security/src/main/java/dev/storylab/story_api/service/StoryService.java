package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.dto.UpdateStoryRequest;
import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.entity.User;
import dev.storylab.story_api.entity.Role;
import dev.storylab.story_api.exception.StoryNotFoundException;
import dev.storylab.story_api.repository.StoryRepository;
import dev.storylab.story_api.repository.UserRepository;
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

@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    public StoryService(StoryRepository storyRepository, UserRepository userRepository) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
    }

    public StoryResponse create(CreateStoryRequest request) {
        User authenticatedUser = getAuthenticatedUser();

        verifyCanPerformAction(authenticatedUser);

        LocalDateTime now = LocalDateTime.now();
        Story story = new Story();


        story.setTitle(request.title());
        story.setSummary(request.summary());
        story.setContent(request.content());
        story.setStatus(request.status());
        story.setCreatedAt(now);
        story.setUpdatedAt(now);
        story.setAuthor(authenticatedUser);

        Story savedStory = storyRepository.save(story);

        return toResponse(savedStory);
    }

    public List<StoryResponse> getAllStories() {
        Optional<User> currentUser = getCurrentUser();

        if (currentUser.isEmpty()) {
            return getPublishedStories();
        }

        User user = currentUser.get();

        if (user.getRole() == Role.ADMIN) {
            return storyRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        if (user.getRole() == Role.AUTHOR) {
            return storyRepository.findAll()
                    .stream()
                    .filter(story ->
                            story.getStatus() == StoryStatus.PUBLISHED
                                    || Objects.equals(story.getAuthor().getId(),
                                    user.getId())
                    )
                    .map(this::toResponse)
                    .toList();
        }
        return getPublishedStories();
    }

    public StoryResponse getStoryById(Long id) {
        Story story = findStoryById(id);

            if (story.getStatus() == StoryStatus.PUBLISHED) {
                return toResponse(story);
            }

            Optional<User> currentUser = getCurrentUser();

            if (currentUser.isEmpty()) {
                throw new StoryNotFoundException(id);
            }

            User user = currentUser.get();

            boolean admin = user.getRole() == Role.ADMIN;
            boolean owner = Objects.equals(story.getAuthor().getId(), user.getId());

            if (!admin && !owner) {
                throw new StoryNotFoundException(id);
            }

            return toResponse(story);
    }

    public List<StoryResponse> getStoriesByStatus(StoryStatus status) {
        if(status.equals(StoryStatus.PUBLISHED)){
            return storyRepository.findByStatus(StoryStatus.PUBLISHED)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        Optional<User> currentUser = getCurrentUser();

        if (currentUser.isEmpty()) {
            return List.of();
        }

        User user = currentUser.get();

        if (user.getRole() == Role.ADMIN) {
            return storyRepository.findByStatus(StoryStatus.DRAFT)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        if (user.getRole() == Role.AUTHOR) {
            return storyRepository.findByStatusAndAuthor(StoryStatus.DRAFT, user)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return List.of();
    }

    public StoryResponse updateStory(Long id, UpdateStoryRequest request) {
        Story story = findStoryById(id);
        User authenticatedUser = getAuthenticatedUser();

        verifyCanPerformAction(story, authenticatedUser);

        story.setTitle(request.title());
        story.setSummary(request.summary());
        story.setContent(request.content());
        story.setStatus(request.status());
        story.setUpdatedAt(LocalDateTime.now());

        Story updatedStory = storyRepository.save(story);

        return toResponse(updatedStory);
    }

    public void deleteStory(Long id) {
        Story story = findStoryById(id);
        User authenticatedUser = getAuthenticatedUser();

        verifyCanPerformAction(story, authenticatedUser);

        storyRepository.delete(story);
    }

    private Story findStoryById(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(
                        () -> new StoryNotFoundException(id)
                );
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Authentication is required");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User " + username + " was not found.")
                );
    }

    private StoryResponse toResponse(Story story) {
        return new StoryResponse(
                story.getId(),
                story.getTitle(),
                story.getSummary(),
                story.getContent(),
                story.getStatus(),
                story.getAuthor().getUsername(),
                story.getCreatedAt(),
                story.getUpdatedAt()
        );
    }

    private void verifyCanPerformAction(Story story, User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getRole() == Role.ADMIN;

        boolean isOwner = Objects.equals(story.getAuthor().getId(),
                authenticatedUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to perform this action.");
        }
    }

    private void verifyCanPerformAction(User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getRole() == Role.ADMIN;
        boolean isAuthor = authenticatedUser.getRole() == Role.AUTHOR;

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("You are not allowed to perform this action.");
        }
    }

    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        return userRepository.findByUsername(authentication.getName());
    }

    private List<StoryResponse> getPublishedStories() {
        return storyRepository
                .findByStatus(StoryStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
