package dev.storylab.userservice.repository;

import dev.storylab.userservice.entity.User;
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
                1L);

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
                    1L)
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
                        1L)
        );

        boolean exists = userRepository.existsByUsername("Dylan20");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExists() {
        boolean exists = userRepository.existsByUsername("nonexistent");

        assertFalse(exists);
    }

    private User createUser(String username, Long accountId){
        User user = new User();

        user.setUsername(username);
        user.setAccountId(accountId);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    @Test
    void shouldFindUserByAccountId(){
        userRepository.saveAndFlush(
                createUser("Dylan20",
                        1L)
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
                        1L)
        );

        boolean exists = userRepository.existsByAccountId(1L);

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenAccountIdDoesNotExists() {
        boolean exists = userRepository.existsByAccountId(3L);

        assertFalse(exists);
    }
}
