package dev.storylab.story_api.controller;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.dto.UpdateStoryRequest;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.exception.ErrorResponse;
import dev.storylab.story_api.service.StoryService;
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Story created successfully",
                    content = @Content(
                            schema = @Schema(implementation = StoryResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Story request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Authenticated user does not have AUTHOR or ADMIN privileges",
                    content = @Content
            )
    })
    public StoryResponse createStory(@Valid @RequestBody CreateStoryRequest request){
        return storyService.create(request);
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Story updated successfully",
                    content = @Content(
                            schema = @Schema(implementation = StoryResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid update request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Caller cannot modify this Story",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Story not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Story deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Caller cannot delete this Story",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Story not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public void deleteStory(@PathVariable Long id){
        storyService.deleteStory(id);
    }
}
