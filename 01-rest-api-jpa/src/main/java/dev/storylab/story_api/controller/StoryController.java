package dev.storylab.story_api.controller;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.dto.UpdateStoryRequest;
import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<StoryResponse> getStories(@RequestParam(required = false)
                                              StoryStatus status) {
        if (status == null)
            return storyService.getAllStories();

        return storyService.getStoriesByStatus(status);
    }

    @GetMapping("/{id}")
    public StoryResponse getStoryById(@PathVariable Long id){
        return storyService.getStoryById(id);
    }

    @PutMapping("/{id}")
    public StoryResponse updateStory(@PathVariable Long id,
                                     @Valid @RequestBody UpdateStoryRequest request
    ){
        return storyService.updateStory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStory(@PathVariable Long id){
        storyService.deleteStory(id);
    }
}
