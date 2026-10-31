package dev.storylab.story_api.repository;

import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.entity.User;
import dev.storylab.story_api.entity.Role;
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

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveStory(){
        LocalDateTime now = LocalDateTime.now();
        User author = userRepository.saveAndFlush(createUser("Alice50", Role.AUTHOR));
        Story story = new Story();

        story.setTitle("The Last Train");
        story.setSummary("A short mystery story.");
        story.setContent("It was almost his birthday when...");
        story.setStatus(StoryStatus.DRAFT);
        story.setAuthor(author);
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
        User author = userRepository.saveAndFlush(createUser("Leslie70", Role.AUTHOR));
        storyRepository.save(
                createStory("The Silent House",
                        StoryStatus.DRAFT, author)
        );

        storyRepository.save(
                createStory("The Good Cousin",
                        StoryStatus.DRAFT, author)
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
        User author = userRepository.saveAndFlush(createUser("Leslie70", Role.AUTHOR));

        storyRepository.saveAndFlush(
                createStory("The Silent House",
                        StoryStatus.DRAFT, author)
        );

        List<Story> result =
                storyRepository.findByStatus(StoryStatus.PUBLISHED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private Story createStory(String title,
                              StoryStatus status,
                              User author) {
        LocalDateTime timestamp = LocalDateTime.now();
        Story story = new Story();

        // story.setId(id);
        story.setTitle(title);
        story.setStatus(status);
        story.setAuthor(author);
        story.setSummary("A summary");
        story.setContent("Story content");
        story.setCreatedAt(timestamp);
        story.setUpdatedAt(timestamp);

        return story;
    }

    private User createUser(String username, Role role) {
        User user = new User();

        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }
}
