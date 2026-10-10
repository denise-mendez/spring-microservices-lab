package dev.storylab.story_api.controller;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.service.StoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoryController.class)
public class StoryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private StoryService storyService;

    @Test
    void createStory_201() throws Exception {
        LocalDateTime now= LocalDateTime.now();

        StoryResponse serviceResponse = new StoryResponse(
          1L,
                "The Last Train",
                "A short mystery story.",
                "It was almost his birthday when...",
                StoryStatus.DRAFT,
                now,
                now
        );

        when(storyService.create(any(CreateStoryRequest.class)))
                .thenReturn(serviceResponse);

        String requestBody = """
                {
                "title": "The Last Train",
                "summary": "A short mystery story.",
                "content": "It was almost his birthday when...",
                "status": "DRAFT"
                }
                """;

        mockMvc.perform(
          post("/api/stories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.title")
                        .value("The Last Train"))
                .andExpect(jsonPath("$.summary")
                        .value("A short mystery story."))
                .andExpect(jsonPath("$.content")
                        .value("It was almost his birthday when..."))
                .andExpect(jsonPath("$.status")
                        .value("DRAFT"));

        verify(storyService)
                .create(any(CreateStoryRequest.class));
    }

    @Test
    void returnBadRequestWhenTitleIsBlank() throws Exception {
        String requestBody = """
                {
                "title": "",
                "summary": "A short mystery story.",
                "content": "It was almost his birthday when...",
                "status": "DRAFT"
                }
                """;

        mockMvc.perform(
                post("/api/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
                .andExpect(status().isBadRequest());

        verify(storyService, never())
                .create(any(CreateStoryRequest.class));
    }

    @Test
    void returnBadRequestWhenContentIsBlank() throws Exception {
        String requestBody = """
                {
                "title": "The Last Train",
                "summary": "A short mystery story.",
                "content": "",
                "status": "DRAFT"
                }
                """;

        mockMvc.perform(
                        post("/api/stories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verify(storyService, never())
                .create(any(CreateStoryRequest.class));
    }

    @Test
    void returnBadRequestWhenStatusIsInvalid() throws Exception {
        String requestBody = """
                {
                "title": "The Last Train",
                "summary": "A short mystery story.",
                "content": "It was almost his birthday when...",
                "status": "FINISHED"
                }
                """;

        mockMvc.perform(
                        post("/api/stories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verify(storyService, never())
                .create(any(CreateStoryRequest.class));
    }

    @Test
    void returnBadRequestWhenStatusIsMissing() throws Exception {
        String requestBody = """
                {
                "title": "The Last Train",
                "summary": "A short mystery story.",
                "content": "It was almost his birthday when...",
                }
                """;

        mockMvc.perform(
                        post("/api/stories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verify(storyService, never())
                .create(any(CreateStoryRequest.class));
    }
}
