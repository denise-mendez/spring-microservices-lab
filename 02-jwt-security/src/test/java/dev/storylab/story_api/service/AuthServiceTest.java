package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.RegisterRequest;
import dev.storylab.story_api.dto.UserResponse;
import dev.storylab.story_api.entity.User;
import dev.storylab.story_api.exception.UsernameAlreadyExistsException;
import dev.storylab.story_api.repository.UserRepository;
import dev.storylab.story_api.entity.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest(
                "Luz15", "Pass15!good"
        );
        when(userRepository.existsByUsername("Luz15"))
                .thenReturn(false);

        when(passwordEncoder.encode("Pass15!good"))
                .thenReturn("encondedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserResponse response = authService.register(request);

        assertAll(
                () -> assertEquals(1L, response.id()),
                () -> assertEquals("Luz15", response.username()),
                () -> assertEquals(Role.USER, response.role()),
                () -> assertNotNull(response.createdAt())
        );

        verify(userRepository).existsByUsername("Luz15");
        verify(passwordEncoder).encode("Pass15!good");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void storeEncodedPassword() {
        RegisterRequest request = new RegisterRequest(
                "Luz15", "Pass15!good"
        );
        when(userRepository.existsByUsername("Luz15"))
                .thenReturn(false);

        when(passwordEncoder.encode("Pass15!good"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        authService.register(request);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("encodedPassword", savedUser.getPassword());

        assertNotEquals("Pass15!good", savedUser.getPassword());
    }

    @Test
    void assignUserRoleDuringRegistration() {
        RegisterRequest request = new RegisterRequest(
                "Luz15", "Pass15!good"
        );
        when(userRepository.existsByUsername("Luz15"))
                .thenReturn(false);

        when(passwordEncoder.encode("Pass15!good"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        authService.register(request);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void throwWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Luz15", "Pass15!good"
        );

        when(userRepository.existsByUsername("Luz15"))
                .thenReturn(true);

        UsernameAlreadyExistsException exception =
                assertThrows(UsernameAlreadyExistsException.class,
                        () -> authService.register(request));

        assertEquals("Username Luz15 already exists", exception.getMessage());

        verify(userRepository).existsByUsername("Luz15");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never())
                .save(any(User.class));
    }
}
