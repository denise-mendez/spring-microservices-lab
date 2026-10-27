package dev.storylab.story_api.controller;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.dto.UpdateStoryRequest;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
@Tag(
        name = "Stories",
        description = "Story creation, retrieval and management"
)
public class StoryController {
    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a Story",
            description = "Creates a Story owned by the authenticated AUTHOR or ADMIN.",
            security = {
                    @SecurityRequirement(
                            name = "bearerAuth"
                    )
            }
    )
    public StoryResponse createStory(@Valid @RequestBody CreateStoryRequest request){
        return storyService.create(request);
    }

    @GetMapping
    @Operation(
            summary = "Get Stories",
            description = "Returns stories visible to the current caller."
    )
    public List<StoryResponse> getStories(@RequestParam(required = false)
                                              StoryStatus status) {
        if (status == null)
            return storyService.getAllStories();

        return storyService.getStoriesByStatus(status);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Story by Id",
            description = "Returns te Story when it is visible to the caller."
    )
    public StoryResponse getStoryById(@PathVariable Long id){
        return storyService.getStoryById(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a Story",
            description = "Updates a Story when the authenticated caller is the owning AUTHOR or an ADMIN.",
            security = {
                    @SecurityRequirement(
                            name = "bearerAuth"
                    )
            }
    )
    public StoryResponse updateStory(@PathVariable Long id,
                                     @Valid @RequestBody UpdateStoryRequest request
    ){
        return storyService.updateStory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a Story",
            description = "Deletes a Story when the authenticated caller is the owning AUTHOR or an ADMIN.",
            security = {
                    @SecurityRequirement(
                            name = "bearerAuth"
                    )
            }
    )
    public void deleteStory(@PathVariable Long id){
        storyService.deleteStory(id);
    }
}
