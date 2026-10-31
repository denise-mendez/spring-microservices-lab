package dev.storylab.story_api.security;

import dev.storylab.story_api.controller.StoryController;
import dev.storylab.story_api.config.SecurityConfig;
import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.dto.UpdateStoryRequest;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.exception.GlobalExceptionHandler;
import dev.storylab.story_api.service.StoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoryController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
public class StoryEndpointSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StoryService storyService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void anonymousShouldReadStories()
            throws Exception {

        when(storyService.getAllStories())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/stories")
                )
                .andExpect(status().isOk());
    }

    @Test
    void anonymousShouldNotCreateStory()
            throws Exception {

        mockMvc.perform(
                        post("/api/stories")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStoryRequest())
                )
                .andExpect(status().isUnauthorized());

        verify(storyService, never())
                .create(
                        any(CreateStoryRequest.class)
                );
    }

    @Test
    @WithMockUser(
            username = "reader",
            roles = "USER"
    )
    void userShouldNotCreateStory()
            throws Exception {

        mockMvc.perform(
                        post("/api/stories")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStoryRequest())
                )
                .andExpect(status().isForbidden());

        verify(storyService, never())
                .create(
                        any(CreateStoryRequest.class)
                );
    }

    @Test
    @WithMockUser(
            username = "clara55",
            roles = "AUTHOR"
    )
    void authorShouldCreateStory()
            throws Exception {

        when(
                storyService.create(
                        any(CreateStoryRequest.class)
                )
        ).thenReturn(storyResponse());

        mockMvc.perform(
                        post("/api/stories")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStoryRequest())
                )
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(
            username = "admin",
            roles = "ADMIN"
    )
    void adminShouldCreateStory()
            throws Exception {

        when(
                storyService.create(
                        any(CreateStoryRequest.class)
                )
        ).thenReturn(storyResponse());

        mockMvc.perform(
                        post("/api/stories")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStoryRequest())
                )
                .andExpect(status().isCreated());
    }

    @Test
    void anonymousShouldNotUpdateStory()
            throws Exception {

        mockMvc.perform(
                        put("/api/stories/{id}", 1L)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStoryRequest())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "reader",
            roles = "USER"
    )
    void userShouldNotUpdateStory()
            throws Exception {

        mockMvc.perform(
                        put("/api/stories/{id}", 1L)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStoryRequest())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "clara55",
            roles = "AUTHOR"
    )
    void authorShouldReachUpdateEndpoint()
            throws Exception {

        when(
                storyService.updateStory(
                        anyLong(),
                        any(UpdateStoryRequest.class)
                )
        ).thenReturn(storyResponse());

        mockMvc.perform(
                        put("/api/stories/{id}", 1L)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStoryRequest())
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "admin",
            roles = "ADMIN"
    )
    void adminShouldReachUpdateEndpoint()
            throws Exception {

        when(
                storyService.updateStory(
                        anyLong(),
                        any(UpdateStoryRequest.class)
                )
        ).thenReturn(storyResponse());

        mockMvc.perform(
                        put("/api/stories/{id}", 1L)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(validStoryRequest())
                )
                .andExpect(status().isOk());
    }

    @Test
    void anonymousShouldNotDeleteStory()
            throws Exception {

        mockMvc.perform(
                        delete("/api/stories/{id}", 1L)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "reader",
            roles = "USER"
    )
    void userShouldNotDeleteStory()
            throws Exception {

        mockMvc.perform(
                        delete("/api/stories/{id}", 1L)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "clara55",
            roles = "AUTHOR"
    )
    void authorShouldReachDeleteEndpoint()
            throws Exception {

        doNothing()
                .when(storyService)
                .deleteStory(1L);

        mockMvc.perform(
                        delete("/api/stories/{id}", 1L)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(
            username = "admin",
            roles = "ADMIN"
    )
    void adminShouldReachDeleteEndpoint()
            throws Exception {

        doNothing()
                .when(storyService)
                .deleteStory(1L);

        mockMvc.perform(
                        delete("/api/stories/{id}", 1L)
                )
                .andExpect(status().isNoContent());
    }

    private String validStoryRequest() {
        return """
{
"title": "Security Story",
"summary": "Security summary",
"content": "Security content",
"status": "DRAFT"
}
""";
    }

    private StoryResponse storyResponse() {
        LocalDateTime timestamp = LocalDateTime.now();

        return new StoryResponse(
                1L,
                "Security Story",
                "Security summary",
                "Security content",
                StoryStatus.DRAFT,
                "clara55",
                timestamp,
                timestamp
        );
    }
}
