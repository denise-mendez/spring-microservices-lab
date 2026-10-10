package dev.storylab.story_api.controller;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponse createStory(@Valid @RequestBody CreateStoryRequest request){
        return storyService.create(request);
    }
}
