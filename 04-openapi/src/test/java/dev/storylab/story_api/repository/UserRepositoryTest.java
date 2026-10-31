package dev.storylab.story_api.repository;

import dev.storylab.story_api.entity.User;
import dev.storylab.story_api.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

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
                "encodedPassword",
                Role.USER);

        User savedUser = userRepository.saveAndFlush(user);

        assertAll(
                () -> assertEquals("Dylan20", savedUser.getUsername()),
                () -> assertEquals("encodedPassword", savedUser.getPassword()),
                () -> assertEquals(Role.USER, savedUser.getRole()),
                () -> assertNotNull(savedUser.getCreatedAt())
        );
    }

    @Test
    void shouldFindUserByUsername(){
        userRepository.saveAndFlush(
            createUser("Dylan20",
                    "encodedPassword",
                    Role.USER)
        );

        Optional<User> result = userRepository.findByUsername("Dylan20");
        assertTrue(result.isPresent());

        User user = result.orElseThrow();
       assertAll(
               () -> assertEquals("Dylan20", user.getUsername()),
               () -> assertEquals(Role.USER, user.getRole())
        );

        assertEquals(Role.USER, user.getRole());
        assertEquals("Dylan20", user.getUsername());
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        userRepository.saveAndFlush(
                createUser("Dylan20",
                        "encodedPassword",
                        Role.USER)
        );

        boolean exists = userRepository.existsByUsername("Dylan20");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExists() {
        boolean exists = userRepository.existsByUsername("nonexistent");

        assertFalse(exists);
    }

    private User createUser(String username, String password, Role role){
        User user = new User();

        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }
}
