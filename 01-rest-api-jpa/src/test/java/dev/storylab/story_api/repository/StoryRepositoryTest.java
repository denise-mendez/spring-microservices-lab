package dev.storylab.story_api.repository;

import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.exception.StoryNotFoundException;
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
        Story story = new Story();

        story.setTitle("The Last Train");
        story.setSummary("A short mystery story.");
        story.setContent("It was almost his birthday when...");
        story.setStatus(StoryStatus.DRAFT);
        story.setCreatedAt(now);
        story.setUpdatedAt(now);

        Story savedStory = storyRepository.save(story);

        Optional<Story> retrievedStory = storyRepository.findById(savedStory.getId());

        assertTrue(retrievedStory.isPresent());

        Story result = retrievedStory.orElseThrow();

        assertAll(
                () -> assertNotNull(result.getId()),
                () ->assertEquals("The Last Train",
                        result.getTitle()),
                () ->assertEquals("A short mystery story.",
                        result.getSummary()),
                () ->assertEquals("It was almost his birthday when...",
                        result.getContent()),
                () -> assertEquals(StoryStatus.DRAFT,
                        result.getStatus()),
                () -> assertNotNull(result.getCreatedAt())
        );
    }

    @Test
    void findStoriesByStatus(){
        storyRepository.save(
                createStory("The Silent House",
                        StoryStatus.DRAFT)
        );

        storyRepository.save(
                createStory("The Good Cousin",
                        StoryStatus.DRAFT)
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
                        StoryStatus.DRAFT)
        );

        List<Story> result =
                storyRepository.findByStatus(StoryStatus.PUBLISHED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private Story createStory(String title,
                              StoryStatus status) {
        LocalDateTime timestamp = LocalDateTime.now();
        Story story = new Story();

        // story.setId(id);
        story.setTitle(title);
        story.setStatus(status);
        story.setSummary("A summary");
        story.setContent("Story content");
        story.setCreatedAt(timestamp);
        story.setUpdatedAt(timestamp);

        return story;
    }
}
