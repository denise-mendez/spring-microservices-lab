package dev.storylab.story_api.repository;

import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
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
}
