package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.AuthResponse;
import dev.storylab.story_api.dto.LoginRequest;
import dev.storylab.story_api.dto.RegisterRequest;
import dev.storylab.story_api.dto.UserResponse;
import dev.storylab.story_api.entity.User;
import dev.storylab.story_api.exception.UsernameAlreadyExistsException;
import dev.storylab.story_api.repository.UserRepository;
import dev.storylab.story_api.entity.Role;

import dev.storylab.story_api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtService = Mockito.mock(JwtService.class);

        authService = new AuthService(userRepository, passwordEncoder,
                authenticationManager, jwtService);
    }

    @Test
    void shouldRegisterUser() {
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

    @Test
    void authenticateUserAndReturnJwt() {
        LoginRequest request = new LoginRequest(
                "Nina39",
                "Str0pe4sdu"
        );

        User user = new User();
        user.setUsername("Nina39");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        when(userRepository.findByUsername("Nina39"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");

        AuthResponse response = authService.login(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals("test-jwt-token", response.token()),
                () -> assertEquals("Bearer", response.type()),
                () -> assertEquals("Nina39", response.username()),
                () -> assertEquals(Role.USER, response.role())
        );

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("Nina39");
        verify(jwtService).generateToken(user);
    }

    @Test
    void authenticateUsingProvidedCredentials() {
        LoginRequest request = new LoginRequest(
                "Nina39",
                "Str0pe4sdu"
        );

        User user = new User();
        user.setUsername("Nina39");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());


        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mock(Authentication.class));

        when(userRepository.findByUsername("Nina39"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);

        authService.login(request);

        verify(authenticationManager).authenticate(captor.capture());

        Authentication authenticationRequest = captor.getValue();

        assertAll(
                () -> assertEquals("Nina39", authenticationRequest.getPrincipal()),
                () -> assertEquals("Str0pe4sdu", authenticationRequest.getCredentials())
        );
    }

    @Test
    void rejectInvalidCredentials() {
        LoginRequest request = new LoginRequest(
                "Nina39",
                "Str0pe4sdu"
        );

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad Credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void throwWhenAuthenticatedUserCannotBeFound() {
        LoginRequest request = new LoginRequest(
                "Nina39",
                "Str0pe4sdu"
        );

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mock(Authentication.class));

        when(userRepository.findByUsername("Nina39"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> authService.login(request));

        assertEquals("User Nina39 was not found.", exception.getMessage());

        verify(jwtService, never()).generateToken(any(User.class));
    }
}
