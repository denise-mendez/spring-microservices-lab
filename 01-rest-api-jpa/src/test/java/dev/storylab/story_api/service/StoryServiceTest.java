package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.repository.StoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StoryServiceTest {
    private StoryService storyService;
    private StoryRepository storyRepository;

    @BeforeEach
    public void setUp(){
        storyRepository = mock(StoryRepository.class);
        storyService = new StoryService(storyRepository);
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
}
