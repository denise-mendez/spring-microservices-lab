package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.dto.UpdateStoryRequest;
import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.exception.StoryNotFoundException;
import dev.storylab.story_api.repository.StoryRepository;
import dev.storylab.story_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StoryServiceTest {
    private StoryService storyService;
    private StoryRepository storyRepository;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        storyRepository = mock(StoryRepository.class);
        userRepository = mock(UserRepository.class);

        storyService = new StoryService(storyRepository, userRepository);
    }

    @Test
    void createStory_201() {
        CreateStoryRequest request = new CreateStoryRequest(
                "The Last Train",
                "A short mystery story.",
                "It was almost his birthday when...",
                StoryStatus.DRAFT
        );

        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation -> {
                   Story story = invocation.getArgument(0);
                   story.setId(1L);
                   return story;
                });

        StoryResponse response = storyService.create(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(1L, response.id()),
                () -> assertEquals("The Last Train", response.title()),
                () -> assertEquals("A short mystery story.", response.summary()),
                () -> assertEquals("It was almost his birthday when...", response.content()),
                () -> assertEquals(StoryStatus.DRAFT, response.status()),
                () -> assertNotNull(response.createdAt()),
                () -> assertNotNull(response.updatedAt())
        );

        //Capture entity sent to the repository
        ArgumentCaptor<Story> storyCaptor = ArgumentCaptor.forClass(Story.class);

        verify(storyRepository, times(1))
                .save(storyCaptor.capture());

        Story persistedStory = storyCaptor.getValue();

        //Assert the Entity was mapped correctly
        assertAll(
                () -> assertEquals("The Last Train", persistedStory.getTitle()),
                () -> assertEquals("A short mystery story.", persistedStory.getSummary()),
                () -> assertEquals("It was almost his birthday when...", persistedStory.getContent()),
                () -> assertEquals(StoryStatus.DRAFT, persistedStory.getStatus()),
                () -> assertNotNull(persistedStory.getCreatedAt()),
                () -> assertNotNull(persistedStory.getUpdatedAt())
        );
    }

    @Test
    void returnAllStories() {
        Story firstStory = createStory(1L,
                "The Silent House",
                StoryStatus.DRAFT);

        Story secondStory = createStory(2L,
                "The Good Cousin",
                StoryStatus.PUBLISHED);

        when(storyRepository.findAll())
                .thenReturn(List.of(firstStory, secondStory));

        List<StoryResponse> result =
                storyService.getAllStories();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals("The Silent House",
                        result.get(0).title()),
                () -> assertEquals(StoryStatus.DRAFT,
                        result.get(0).status()),
                () -> assertEquals(2L, result.get(1).id()),
                () -> assertEquals("The Good Cousin", result.get(1).title()),
                () -> assertEquals(StoryStatus.PUBLISHED,
                        result.get(1).status())
        );

        verify(storyRepository, times(1)).findAll();
    }

    @Test
    void returnEmptyListWhenNoStoriesExist() {
        when(storyRepository.findAll())
                .thenReturn(List.of());

        List<StoryResponse> result =
                storyService.getAllStories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void returnStoryById() {
        Story firstStory = createStory(1L,
                "The Silent House",
                StoryStatus.DRAFT);

        when(storyRepository.findById(1L))
                .thenReturn(Optional.of(firstStory));

        StoryResponse result =
                storyService.getStoryById(1L);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("The Silent House",
                        result.title()),
                () -> assertEquals(StoryStatus.DRAFT,
                        result.status())
        );

        verify(storyRepository, times(1)).findById(1L);
    }

    @Test
    void throwExceptionWhenStoryDoesNotExist() {
        when(storyRepository.findById(99L))
                .thenReturn(Optional.empty());

        StoryNotFoundException exception =
                assertThrows(
                        StoryNotFoundException.class,
                        () -> storyService.getStoryById(99L)
                );

        assertEquals(
                "Story with id 99 not found",
                exception.getMessage()
        );

        verify(storyRepository, times(1)).findById(99L);
    }

    @Test
    void returnStoriesByStatus() {
        Story publishedStory = createStory(
                1L,
                "The blank diary",
                StoryStatus.PUBLISHED
        );

        when(storyRepository.findByStatus(StoryStatus.PUBLISHED))
                .thenReturn(List.of(publishedStory));

        List<StoryResponse> result =
                storyService.getStoriesByStatus(StoryStatus.PUBLISHED);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(
                        1L,
                        result.getFirst().id()
                ),
                () -> assertEquals(
                        "The blank diary",
                        result.getFirst().title()
                ),
                () -> assertEquals(
                        StoryStatus.PUBLISHED,
                        result.getFirst().status()
                )
        );

        verify(storyRepository)
                .findByStatus(StoryStatus.PUBLISHED);
    }

    @Test
    void returnEmptyListWhenNoStoriesMatchStatus() {
        when(storyRepository.findByStatus(StoryStatus.PUBLISHED))
        .thenReturn(List.of());

        List<StoryResponse> result =
                storyService.getStoriesByStatus(StoryStatus.PUBLISHED);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(storyRepository, times(1))
                .findByStatus(StoryStatus.PUBLISHED);
    }

    @Test
    void  updateStory() {
        Story existingStory = createStory(
                1L,
                "The blank diary",
                StoryStatus.PUBLISHED
        );

        LocalDateTime originalCreatedAt =
                existingStory.getCreatedAt();

        LocalDateTime  originalUpdatedAt =
                existingStory.getUpdatedAt();

        UpdateStoryRequest request =
                new UpdateStoryRequest(
                        "Updated Title",
                        "New summary",
                        "Extended content",
                        StoryStatus.PUBLISHED
                );

        when(storyRepository.findById(1L))
                .thenReturn(Optional.of(existingStory));

        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        StoryResponse result =
                storyService.updateStory(1L, request);

        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(
                        "Updated Title",
                        result.title()
                ),
                () -> assertEquals(
                        "New summary",
                        result.summary()
                ),
                () -> assertEquals(
                        "Extended content",
                        result.content()
                ),
                () -> assertEquals(
                        StoryStatus.PUBLISHED.name(),
                        result.status().name()
                ),
                () -> assertEquals(
                        originalCreatedAt,
                        result.createdAt()
                ),
                () -> assertTrue(
                        result.updatedAt()
                                .isAfter(originalUpdatedAt)
                )
        );

        ArgumentCaptor<Story> storyCaptor = ArgumentCaptor.forClass(Story.class);

        verify(storyRepository, times((1))).findById(1L);
        verify(storyRepository, times(1))
                .save(storyCaptor.capture());

        Story updatedStory = storyCaptor.getValue();

        assertAll(
                () -> assertEquals(
                        "Updated Title",
                        updatedStory.getTitle()
                ),
                () -> assertEquals(
                        StoryStatus.PUBLISHED.name(),
                        updatedStory.getStatus().name()
                ),
                () -> assertEquals(
                        originalCreatedAt,
                        updatedStory.getCreatedAt()
                )
        );
    }

    @Test
    void throwExceptionWhenUpdatingMissingStory() {
        UpdateStoryRequest request =
                new UpdateStoryRequest(
                        "Updated Title",
                        "New summary",
                        "Extended content",
                        StoryStatus.PUBLISHED
                );

        when(storyRepository.findById(99L))
                .thenReturn(Optional.empty());

        StoryNotFoundException exception =
                assertThrows(
                        StoryNotFoundException.class,
                        () -> storyService.updateStory(99L, request)
                );

        assertEquals(
                "Story with id 99 not found",
                exception.getMessage()
        );

        verify(storyRepository).findById(99L);
        verify(storyRepository, never()).save(any(Story.class));
    }

    @Test
    void shouldDeleteStory() {
        Story existingStory = createStory(
                1L,
                "The blank diary",
                StoryStatus.PUBLISHED
        );

        when(storyRepository.findById(1L))
                .thenReturn(Optional.of(existingStory));

        storyService.deleteStory(1L);

        verify(storyRepository, times(1))
                .findById(1L);
        verify(storyRepository, times(1))
                .delete(existingStory);
    }

    @Test
    void throwExceptionWhenDeletingMissingStory() {
        when(storyRepository.findById(99L))
                .thenReturn(Optional.empty());

        StoryNotFoundException exception =
                assertThrows(
                        StoryNotFoundException.class,
                        () -> storyService.deleteStory(99L)
                );

        assertEquals(
                "Story with id 99 not found",
                exception.getMessage()
        );

        verify(storyRepository).findById(99L);
        verify(storyRepository, never()).save(any(Story.class));
    }

    private Story createStory(Long id, String title,
                              StoryStatus status) {
        LocalDateTime timestamp = LocalDateTime.now();
        Story story = new Story();

        story.setId(id);
        story.setTitle(title);
        story.setStatus(status);
        story.setSummary("A summary");
        story.setContent("Story content");
        story.setCreatedAt(timestamp);
        story.setUpdatedAt(timestamp);

        return story;
    }
}
