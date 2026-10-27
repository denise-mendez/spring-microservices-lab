package dev.storylab.story_api.controller;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.exception.StoryNotFoundException;
import dev.storylab.story_api.security.JwtAuthenticationFilter;
import dev.storylab.story_api.security.JwtService;
import dev.storylab.story_api.service.StoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StoryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private StoryService storyService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void createStory_201() throws Exception {
        LocalDateTime now= LocalDateTime.now();

        StoryResponse serviceResponse = new StoryResponse(
          1L,
                "The Last Train",
                "A short mystery story.",
                "It was almost his birthday when...",
                StoryStatus.DRAFT,
                "Ethan",
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

    @Test
    void returnAllStories() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();

        List<StoryResponse> stories = List.of(
                new StoryResponse(
                        1L,
                        "The Silent House",
                        "A beautiful yellow house",
                        "Only birds chirping can be heard...",
                        StoryStatus.DRAFT,
                        "Ethan",
                        timestamp,
                        timestamp
                ),
                new StoryResponse(
                        2L,
                        "The Good Cousin",
                        "The youngest man",
                        "He teaches the children crafting...",
                        StoryStatus.PUBLISHED,
                        "Ethan",
                        timestamp,
                        timestamp
                )
        );

        when(storyService.getAllStories()).thenReturn(stories);

        mockMvc.perform(get("/api/stories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0].id")
                        .value(1))
                .andExpect(jsonPath("$[0].title")
                        .value("The Silent House"))
                .andExpect(jsonPath("$[0].status")
                        .value(StoryStatus.DRAFT.name()))

                .andExpect(jsonPath("$[1].id")
                        .value(2))
                .andExpect(jsonPath("$[1].title")
                        .value("The Good Cousin"))
                .andExpect(jsonPath("$[1].status")
                        .value(StoryStatus.PUBLISHED.name()));

        verify(storyService, times(1))
                .getAllStories();
    }

    @Test
    void returnEmptyListWhenNoStoriesExist() throws Exception {
        when(storyService.getAllStories())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/stories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(storyService, times(1))
                .getAllStories();
    }

    @Test
    void returnStoryById() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();

        StoryResponse story =
                new StoryResponse(
                        1L,
                        "The Silent House",
                        "A beautiful yellow house",
                        "Only birds chirping can be heard...",
                        StoryStatus.DRAFT,
                        "Ethan",
                        timestamp,
                        timestamp
                );

        when(storyService.getStoryById(1L)).thenReturn(story);

        mockMvc.perform(get("/api/stories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.title")
                        .value("The Silent House"))
                .andExpect(jsonPath("$.status")
                        .value(StoryStatus.DRAFT.name()));


        verify(storyService, times(1))
                .getStoryById(1L);
    }

    @Test
    void returnNotFoundWhenStoryDoesNotExist() throws Exception {
        when(storyService.getStoryById(99L))
                .thenThrow(new StoryNotFoundException(99L));

        mockMvc.perform(get("/api/stories/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.error")
                                .value("STORY_NOT_FOUND")
                )
                        .andExpect(
                                jsonPath("$.message")
                                        .value(
                                                "Story with id 99 not found"
                                        )
                        );

        verify(storyService, times(1))
                .getStoryById(99L);
    }
}
