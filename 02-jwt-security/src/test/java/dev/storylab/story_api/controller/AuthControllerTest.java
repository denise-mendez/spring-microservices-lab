package dev.storylab.story_api.controller;

import dev.storylab.story_api.config.SecurityConfig;
import dev.storylab.story_api.dto.RegisterRequest;
import dev.storylab.story_api.dto.UserResponse;
import dev.storylab.story_api.entity.Role;
import dev.storylab.story_api.exception.GlobalExceptionHandler;
import dev.storylab.story_api.exception.UsernameAlreadyExistsException;
import dev.storylab.story_api.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({
        GlobalExceptionHandler.class,
    SecurityConfig.class
})
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void registerUser() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();

        UserResponse response = new UserResponse(
                1L,
                "Hugo10",
                Role.USER,
                createdAt
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                "username": "Hugo10",
                "password": "Str0ng-4aswd"
                }
                """;

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id").value(1)
                )
                .andExpect(
                        jsonPath("$.username").value("Hugo10")
                )
                .andExpect(
                        jsonPath("$.role").value(Role.USER.name())
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                )
                .andExpect(
                        jsonPath("$.password").doesNotExist()
                );

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void returnBadRequestWhenPasswordIsTooShort() throws Exception
    {
        String requestBody = """
                {
                "username": "Hugo10",
                "password": "a"
                }
                """;

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void returnBadRequestWhenUsernameIsBlank() throws Exception
    {
        String requestBody = """
                {
                "username": "",
                "password": "Str0ng-4aswd"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void returnConflictWhenUsernameAlreadyExists() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new UsernameAlreadyExistsException("Hugo10"));

        String requestBody = """
                {
                "username": "Hugo10",
                "password": "Str0ng-4aswd"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.status").value("409")
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("USERNAME_ALREADY_EXISTS")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Username Hugo10 already exists")
                );

        verify(authService)
                .register(any(RegisterRequest.class));
    }
}