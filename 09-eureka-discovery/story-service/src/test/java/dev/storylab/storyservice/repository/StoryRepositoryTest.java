package dev.storylab.storyservice.repository;

import dev.storylab.storyservice.entity.Story;
import dev.storylab.storyservice.entity.StoryStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class StoryRepositoryTest {
    @Autowired
    private StoryRepository storyRepository;

    @Test
    void saveStory(){
        LocalDateTime now = LocalDateTime.now();

        Story story = createStory("The Last Train",
                StoryStatus.DRAFT,
                5L);

        Story savedStory = storyRepository.saveAndFlush(story);

        Optional<Story> retrievedStory = storyRepository.findById(savedStory.getId());

        assertTrue(retrievedStory.isPresent());

        Story result = retrievedStory.orElseThrow();

        assertAll(
                () -> assertNotNull(result.getId()),
                () ->assertEquals("The Last Train",
                        result.getTitle()),
                () ->assertEquals("A summary",
                        result.getSummary()),
                () ->assertEquals("Story content",
                        result.getContent()),
                () -> assertEquals(StoryStatus.DRAFT,
                        result.getStatus()),
                () -> assertNotNull(result.getCreatedAt()),
                () -> assertNotNull(result.getUpdatedAt())
        );
    }

    @Test
    void findStoriesByStatus(){
        Long authorId = 8L;
        storyRepository.save(
                createStory("The Silent House",
                        StoryStatus.DRAFT, authorId)
        );

        storyRepository.save(
                createStory("The Good Cousin",
                        StoryStatus.DRAFT, authorId)
        );

        storyRepository.flush();

        List<Story> publishedStories =
                storyRepository.findByStatus(StoryStatus.DRAFT);

        assertAll(
                () -> assertEquals(
                        2,
                        publishedStories.size()
                ),
                () -> assertEquals(
                        "The Silent House",
                        publishedStories.getFirst().getTitle()
                ),
                () -> assertEquals(
                        StoryStatus.DRAFT.name(),
                        publishedStories.getFirst().getStatus().name()
                )
        );
    }

    @Test
    void returnEmptyListWhenNoStoriesMatchStatus() {
        storyRepository.saveAndFlush(
                createStory("The Silent House",
                        StoryStatus.DRAFT, 17L)
        );

        List<Story> result =
                storyRepository.findByStatus(StoryStatus.PUBLISHED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private Story createStory(String title,
                              StoryStatus status,
                              Long authorId) {
        LocalDateTime timestamp = LocalDateTime.now();
        Story story = new Story();

        // story.setId(id);
        story.setTitle(title);
        story.setStatus(status);
        story.setAuthorId(authorId);
        story.setSummary("A summary");
        story.setContent("Story content");
        story.setCreatedAt(timestamp);
        story.setUpdatedAt(timestamp);

        return story;
    }
}
