package dev.storylab.story_api.security;

import dev.storylab.story_api.dto.CreateStoryRequest;
import dev.storylab.story_api.dto.StoryResponse;
import dev.storylab.story_api.dto.UpdateStoryRequest;
import dev.storylab.story_api.entity.Role;
import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.entity.User;
import dev.storylab.story_api.exception.StoryNotFoundException;
import dev.storylab.story_api.repository.StoryRepository;
import dev.storylab.story_api.repository.UserRepository;
import dev.storylab.story_api.service.StoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class StoryServiceSecurityTest {
    private StoryRepository storyRepository;
    private UserRepository userRepository;
    private StoryService storyService;

    private User regularUser;
    private User author;
    private User admin;

    private Story publishedStory;
    private Story authorDraft;
    private Story anotherAuthorDraft;

    @BeforeEach
    void setUp() {

        storyRepository = mock(StoryRepository.class);
        userRepository = mock(UserRepository.class);

        storyService = new StoryService(
                storyRepository,
                userRepository
        );

        regularUser = createUser(
                1L,
                "reader",
                Role.USER
        );

        author = createUser(
                2L,
                "mar77",
                Role.AUTHOR
        );

        User anotherAuthor = createUser(
                3L,
                "maria",
                Role.AUTHOR
        );

        admin = createUser(
                4L,
                "admin",
                Role.ADMIN
        );

        publishedStory = createStory(
                10L,
                "Published Story",
                StoryStatus.PUBLISHED,
                anotherAuthor
        );

        authorDraft = createStory(
                11L,
                "Pablo Draft",
                StoryStatus.DRAFT,
                author
        );

        anotherAuthorDraft = createStory(
                12L,
                "Maria Draft",
                StoryStatus.DRAFT,
                anotherAuthor
        );
    }


    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ==================================================
    // GET /api/stories/{id}
    // ==================================================

    @Test
    void anonymousShouldSeePublishedStory() {

        when(storyRepository.findById(10L))
                .thenReturn(Optional.of(publishedStory));

        StoryResponse result =
                storyService.getStoryById(10L);

        assertEquals(
                StoryStatus.PUBLISHED,
                result.status()
        );

        verify(userRepository, never())
                .findByUsername(anyString());
    }

    @Test
    void userShouldSeePublishedStory() {

        authenticateAs(regularUser);

        when(storyRepository.findById(10L))
                .thenReturn(Optional.of(publishedStory));

        StoryResponse result =
                storyService.getStoryById(10L);

        assertEquals(
                StoryStatus.PUBLISHED,
                result.status()
        );
    }

    @Test
    void anonymousShouldNotSeeDraft() {

        when(storyRepository.findById(11L))
                .thenReturn(Optional.of(authorDraft));

        assertThrows(
                StoryNotFoundException.class,
                () -> storyService.getStoryById(11L)
        );
    }

    @Test
    void userShouldNotSeeDraft() {

        authenticateAs(regularUser);
        mockCurrentUser(regularUser);

        when(storyRepository.findById(11L))
                .thenReturn(Optional.of(authorDraft));

        assertThrows(
                StoryNotFoundException.class,
                () -> storyService.getStoryById(11L)
        );
    }

    @Test
    void authorShouldSeeOwnDraft() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(storyRepository.findById(11L))
                .thenReturn(Optional.of(authorDraft));

        StoryResponse result =
                storyService.getStoryById(11L);

        assertAll(
                () -> assertEquals(
                        11L,
                        result.id()
                ),
                () -> assertEquals(
                        "Pablo Draft",
                        result.title()
                ),
                () -> assertEquals(
                        StoryStatus.DRAFT,
                        result.status()
                ),
                () -> assertEquals(
                        "mar77",
                        result.author()
                )
        );
    }

    @Test
    void authorShouldNotSeeAnotherAuthorsDraft() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(storyRepository.findById(12L))
                .thenReturn(
                        Optional.of(anotherAuthorDraft)
                );

        assertThrows(
                StoryNotFoundException.class,
                () -> storyService.getStoryById(12L)
        );
    }

    @Test
    void adminShouldSeeAnyDraft() {

        authenticateAs(admin);
        mockCurrentUser(admin);

        when(storyRepository.findById(12L))
                .thenReturn(
                        Optional.of(anotherAuthorDraft)
                );

        StoryResponse result =
                storyService.getStoryById(12L);

        assertAll(
                () -> assertEquals(
                        "Maria Draft",
                        result.title()
                ),
                () -> assertEquals(
                        StoryStatus.DRAFT,
                        result.status()
                )
        );
    }

    // ==================================================
    // GET /api/stories
    // ==================================================

    @Test
    void anonymousShouldReceivePublishedStoriesOnly() {

        when(
                storyRepository.findByStatus(
                        StoryStatus.PUBLISHED
                )
        ).thenReturn(
                List.of(publishedStory)
        );

        List<StoryResponse> result =
                storyService.getAllStories();

        assertEquals(1, result.size());

        assertEquals(
                StoryStatus.PUBLISHED,
                result.getFirst().status()
        );

        verify(storyRepository, never())
                .findAll();
    }

    @Test
    void userShouldReceivePublishedStoriesOnly() {

        authenticateAs(regularUser);
        mockCurrentUser(regularUser);

        when(
                storyRepository.findByStatus(
                        StoryStatus.PUBLISHED
                )
        ).thenReturn(
                List.of(publishedStory)
        );

        List<StoryResponse> result =
                storyService.getAllStories();

        assertEquals(1, result.size());

        assertEquals(
                StoryStatus.PUBLISHED,
                result.getFirst().status()
        );

        verify(storyRepository, never())
                .findAll();
    }

    @Test
    void authorShouldReceivePublishedAndOwnDrafts() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(
                storyRepository.findByStatus(
                        StoryStatus.PUBLISHED
                )
        ).thenReturn(
                List.of(publishedStory)
        );

        when(
                storyRepository.findByStatusAndAuthor(
                        StoryStatus.DRAFT,
                        author
                )
        ).thenReturn(
                List.of(authorDraft)
        );

        List<StoryResponse> result =
                storyService.getAllStories();

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .anyMatch(story ->
                                story.id().equals(10L)
                        )
        );

        assertTrue(
                result.stream()
                        .anyMatch(story ->
                                story.id().equals(11L)
                        )
        );

        assertFalse(
                result.stream()
                        .anyMatch(story ->
                                story.id().equals(12L)
                        )
        );
    }

    @Test
    void adminShouldReceiveAllStories() {

        authenticateAs(admin);
        mockCurrentUser(admin);

        when(storyRepository.findAll())
                .thenReturn(
                        List.of(
                                publishedStory,
                                authorDraft,
                                anotherAuthorDraft
                        )
                );

        List<StoryResponse> result =
                storyService.getAllStories();

        assertEquals(3, result.size());

        verify(storyRepository).findAll();
    }

    // ==================================================
    // GET /api/stories?status=PUBLISHED
    // ==================================================

    @Test
    void everyoneShouldReceivePublishedStories() {

        when(
                storyRepository.findByStatus(
                        StoryStatus.PUBLISHED
                )
        ).thenReturn(
                List.of(publishedStory)
        );

        List<StoryResponse> result =
                storyService.getStoriesByStatus(
                        StoryStatus.PUBLISHED
                );

        assertEquals(1, result.size());

        assertEquals(
                StoryStatus.PUBLISHED,
                result.getFirst().status()
        );
    }

    // ==================================================
    // GET /api/stories?status=DRAFT
    // ==================================================

    @Test
    void anonymousShouldReceiveEmptyDraftList() {

        List<StoryResponse> result =
                storyService.getStoriesByStatus(
                        StoryStatus.DRAFT
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void userShouldReceiveEmptyDraftList() {

        authenticateAs(regularUser);
        mockCurrentUser(regularUser);

        List<StoryResponse> result =
                storyService.getStoriesByStatus(
                        StoryStatus.DRAFT
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void authorShouldReceiveOwnDraftsOnly() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(
                storyRepository.findByStatusAndAuthor(
                        StoryStatus.DRAFT,
                        author
                )
        ).thenReturn(
                List.of(authorDraft)
        );

        List<StoryResponse> result =
                storyService.getStoriesByStatus(
                        StoryStatus.DRAFT
                );

        assertEquals(1, result.size());

        assertAll(
                () -> assertEquals(
                        "Pablo Draft",
                        result.getFirst().title()
                ),
                () -> assertEquals(
                        "mar77",
                        result.getFirst().author()
                )
        );
    }

    @Test
    void adminShouldReceiveAllDrafts() {

        authenticateAs(admin);
        mockCurrentUser(admin);

        when(
                storyRepository.findByStatus(
                        StoryStatus.DRAFT
                )
        ).thenReturn(
                List.of(
                        authorDraft,
                        anotherAuthorDraft
                )
        );

        List<StoryResponse> result =
                storyService.getStoriesByStatus(
                        StoryStatus.DRAFT
                );

        assertEquals(2, result.size());
    }

    // ==================================================
    // POST
    // ==================================================

    @Test
    void authorShouldCreateStory() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation -> {

                    Story story =
                            invocation.getArgument(0);

                    story.setId(20L);

                    return story;
                });

        StoryResponse result =
                storyService.create(
                        createRequest()
                );

        assertAll(
                () -> assertEquals(
                        20L,
                        result.id()
                ),
                () -> assertEquals(
                        "mar77",
                        result.author()
                ),
                () -> assertEquals(
                        StoryStatus.DRAFT,
                        result.status()
                )
        );

        verify(storyRepository)
                .save(any(Story.class));
    }

    @Test
    void adminShouldCreateStory() {

        authenticateAs(admin);
        mockCurrentUser(admin);

        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation -> {

                    Story story =
                            invocation.getArgument(0);

                    story.setId(21L);

                    return story;
                });

        StoryResponse result =
                storyService.create(
                        createRequest()
                );

        assertEquals(
                "admin",
                result.author()
        );
    }

    @Test
    void userShouldNotCreateStory() {

        authenticateAs(regularUser);
        mockCurrentUser(regularUser);

        assertThrows(
                AccessDeniedException.class,
                () -> storyService.create(
                        createRequest()
                )
        );

        verify(storyRepository, never())
                .save(any(Story.class));
    }

    // ==================================================
    // PUT
    // ==================================================

    @Test
    void authorShouldUpdateOwnStory() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(storyRepository.findById(11L))
                .thenReturn(
                        Optional.of(authorDraft)
                );

        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        StoryResponse result =
                storyService.updateStory(
                        11L,
                        updateRequest()
                );

        assertAll(
                () -> assertEquals(
                        "Updated Title",
                        result.title()
                ),
                () -> assertEquals(
                        StoryStatus.PUBLISHED,
                        result.status()
                )
        );
    }

    @Test
    void authorShouldNotUpdateAnotherAuthorsStory() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(storyRepository.findById(12L))
                .thenReturn(
                        Optional.of(anotherAuthorDraft)
                );

        assertThrows(
                AccessDeniedException.class,
                () -> storyService.updateStory(
                        12L,
                        updateRequest()
                )
        );

        verify(storyRepository, never())
                .save(any(Story.class));
    }

    @Test
    void adminShouldUpdateAnyStory() {

        authenticateAs(admin);
        mockCurrentUser(admin);

        when(storyRepository.findById(12L))
                .thenReturn(
                        Optional.of(anotherAuthorDraft)
                );

        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        StoryResponse result =
                storyService.updateStory(
                        12L,
                        updateRequest()
                );

        assertEquals(
                "Updated Title",
                result.title()
        );
    }

    // ==================================================
    // DELETE
    // ==================================================

    @Test
    void authorShouldDeleteOwnStory() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(storyRepository.findById(11L))
                .thenReturn(
                        Optional.of(authorDraft)
                );

        storyService.deleteStory(11L);

        verify(storyRepository)
                .delete(authorDraft);
    }

    @Test
    void authorShouldNotDeleteAnotherAuthorsStory() {

        authenticateAs(author);
        mockCurrentUser(author);

        when(storyRepository.findById(12L))
                .thenReturn(
                        Optional.of(anotherAuthorDraft)
                );

        assertThrows(
                AccessDeniedException.class,
                () -> storyService.deleteStory(12L)
        );

        verify(storyRepository, never())
                .delete(any(Story.class));
    }

    @Test
    void adminShouldDeleteAnyStory() {

        authenticateAs(admin);
        mockCurrentUser(admin);

        when(storyRepository.findById(12L))
                .thenReturn(
                        Optional.of(anotherAuthorDraft)
                );

        storyService.deleteStory(12L);

        verify(storyRepository)
                .delete(anotherAuthorDraft);
    }

    // ==================================================
    // Helpers
    // ==================================================

    private void authenticateAs(User user) {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_"
                                                + user.getRole().name()
                                )
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    private void mockCurrentUser(User user) {

        when(
                userRepository.findByUsername(
                        user.getUsername()
                )
        ).thenReturn(
                Optional.of(user)
        );
    }

    private User createUser(
            Long id,
            String username,
            Role role
    ) {

        User user = new User();

        user.setId(id);
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setRole(role);

        user.setCreatedAt(
                LocalDateTime.now()
        );

        return user;
    }

    private Story createStory(
            Long id,
            String title,
            StoryStatus status,
            User author
    ) {

        LocalDateTime timestamp =
                LocalDateTime.now();

        Story story = new Story();

        story.setId(id);
        story.setTitle(title);
        story.setSummary("Story summary");
        story.setContent("Story content");
        story.setStatus(status);
        story.setAuthor(author);
        story.setCreatedAt(timestamp);
        story.setUpdatedAt(timestamp);

        return story;
    }

    private CreateStoryRequest createRequest() {

        return new CreateStoryRequest(
                "New Story",
                "New summary",
                "New content",
                StoryStatus.DRAFT
        );
    }

    private UpdateStoryRequest updateRequest() {

        return new UpdateStoryRequest(
                "Updated Title",
                "Updated summary",
                "Updated content",
                StoryStatus.PUBLISHED
        );
    }
}
