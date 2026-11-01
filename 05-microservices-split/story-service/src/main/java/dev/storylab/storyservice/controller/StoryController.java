package dev.storylab.storyservice.controller;

import dev.storylab.storyservice.dto.CreateStoryRequest;
import dev.storylab.storyservice.dto.StoryResponse;
import dev.storylab.storyservice.dto.UpdateStoryRequest;
import dev.storylab.storyservice.entity.StoryStatus;
import dev.storylab.storyservice.exception.ErrorResponse;
import dev.storylab.storyservice.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @GetMapping
    @Operation(
            summary = "Get Stories",
            description = "Returns stories visible to the current caller." +
                    "Anonymous and USER callers see published stories." +
                    "AUTHOR additionally sees their own drafts." +
                    "ADMIN sees all stories."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stories returned successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Story status",
                    content = @Content
            )
    })
    public List<StoryResponse> getStories(@RequestParam(required = false)
                                              StoryStatus status) {
        if (status == null)
            return storyService.getAllStories();

        return storyService.getStoriesByStatus(status);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Story by Id",
            description = "Returns a published Story, an AUTHOR's own draft," +
                    "or any Story for ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Story returned successfully",
                    content = @Content(
                            schema = @Schema(implementation = StoryResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Story does not exists or is not visible to the caller",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public StoryResponse getStoryById(@PathVariable Long id){
        return storyService.getStoryById(id);
    }
}
