package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.exception.StoryNotFoundException;
import dev.storylab.story_api.repository.StoryRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public StoryResponse create(CreateStoryRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Story story = new Story();

        story.setTitle(request.title());
        story.setSummary(request.summary());
        story.setContent(request.content());
        story.setStatus(request.status());
        story.setCreatedAt(now);
        story.setUpdatedAt(now);

        Story savedStory = storyRepository.save(story);

        return toResponse(savedStory);
    }

    public List<StoryResponse> getAllStories() {
        return storyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public StoryResponse getStoryById(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(
                        () -> new StoryNotFoundException(id)
                );

        return toResponse(story);
    }

    private StoryResponse toResponse(Story story) {
        return new StoryResponse(
                story.getId(),
                story.getTitle(),
                story.getSummary(),
                story.getContent(),
                story.getStatus(),
                story.getCreatedAt(),
                story.getUpdatedAt()
        );
    }
}
