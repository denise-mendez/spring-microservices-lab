package dev.storylab.userservice.repository;

import dev.storylab.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser(){
        User user = createUser("Dylan20",
                1L, "dylan@example.com");

        User savedUser = userRepository.saveAndFlush(user);

        assertAll(
                () -> assertEquals("Dylan20", savedUser.getUsername()),
                () -> assertNotNull(savedUser.getCreatedAt())
        );
    }

    @Test
    void shouldFindUserByUsername(){
        userRepository.saveAndFlush(
            createUser("Dylan20",
                    1L,
                    "dylan@example.com")
        );

        Optional<User> result = userRepository.findByUsername("Dylan20");
        assertTrue(result.isPresent());

        User user = result.orElseThrow();
       assertAll(
               () -> assertEquals("Dylan20", user.getUsername()),
               () -> assertEquals(1L, user.getAccountId())
        );
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        userRepository.saveAndFlush(
                createUser("Dylan20",
                        1L,
                        "dylan@example.com")
        );

        boolean exists = userRepository.existsByUsername("Dylan20");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExists() {
        boolean exists = userRepository.existsByUsername("nonexistent");

        assertFalse(exists);
    }

    private User createUser(String username, Long accountId, String email){
        User user = new User();

        user.setUsername(username);
        user.setAccountId(accountId);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    @Test
    void shouldFindUserByAccountId(){
        userRepository.saveAndFlush(
                createUser("Dylan20",
                        1L,
                        "dylan@example.com")
        );

        Optional<User> result = userRepository.findByAccountId(1L);
        assertTrue(result.isPresent());

        User user = result.orElseThrow();
        assertAll(
                () -> assertEquals("Dylan20", user.getUsername()),
                () -> assertEquals(1L, user.getAccountId())
        );
    }

    @Test
    void shouldReturnTrueWhenAccountIdExists() {
        userRepository.saveAndFlush(
                createUser("Dylan20",
                        1L,
                        "dylan@example.com")
        );

        boolean exists = userRepository.existsByAccountId(1L);

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenAccountIdDoesNotExists() {
        boolean exists = userRepository.existsByAccountId(3L);

        assertFalse(exists);
    }

    @Test
    void shouldFindUserByEmail(){
        User user = createUser("Maya55", 7L, "maya@example.com");
        userRepository.saveAndFlush(user);
        Optional<User> result = userRepository.findByEmail("maya@example.com");
        assertTrue(result.isPresent());
        assertEquals("maya@example.com", result.get().getEmail());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        userRepository.saveAndFlush(
                createUser("maya55",
                        1L,
                        "maya@example.com")
        );

        boolean exists = userRepository.existsByEmail("maya@example.com");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExists() {
        boolean exists = userRepository.existsByEmail("adifferentemail@example.com");

        assertFalse(exists);
    }
}
