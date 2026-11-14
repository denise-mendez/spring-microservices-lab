package dev.storylab.storyservice.controller;

import dev.storylab.storyservice.dto.CreateStoryRequest;
import dev.storylab.storyservice.dto.StoryResponse;
import dev.storylab.storyservice.dto.UpdateStoryRequest;
import dev.storylab.storyservice.entity.StoryStatus;
import dev.storylab.storyservice.exception.ErrorResponse;
import dev.storylab.storyservice.security.CurrentUser;
import dev.storylab.storyservice.security.CurrentUserProvider;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/stories")
@Tag(
        name = "Stories",
        description = "Story creation, retrieval and management"
)
public class StoryController {
    private final StoryService storyService;
    private final CurrentUserProvider currentUserProvider;

    public StoryController(StoryService storyService, CurrentUserProvider currentUserProvider) {
        this.storyService = storyService;
        this.currentUserProvider = currentUserProvider;
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
        CurrentUser currentUser = currentUserProvider.getRequiredUser();
        return storyService.create(request, currentUser);
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
        Optional<CurrentUser> currentUser = currentUserProvider.getCurrentUser();

        if (status != null)
            return storyService.getStoriesByStatus(status, currentUser.orElse(null));

        return storyService.getAllStories(currentUser.orElse(null));
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
        return storyService.getStoryById(id, currentUserProvider.getCurrentUser().orElse(null));
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
        CurrentUser currentUser = currentUserProvider.getRequiredUser();
        return storyService.updateStory(id, request, currentUser);
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
        CurrentUser currentUser = currentUserProvider.getRequiredUser();
        storyService.deleteStory(id, currentUser);
    }
}
