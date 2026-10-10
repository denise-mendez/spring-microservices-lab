package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

        return new StoryResponse(
                savedStory.getId(),
                savedStory.getTitle(),
                savedStory.getSummary(),
                savedStory.getContent(),
                savedStory.getStatus(),
                savedStory.getCreatedAt(),
                savedStory.getUpdatedAt()
        );
    }
}
