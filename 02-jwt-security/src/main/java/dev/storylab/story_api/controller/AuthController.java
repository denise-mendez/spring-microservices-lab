package dev.storylab.story_api.controller;

import dev.storylab.story_api.dto.RegisterRequest;
import dev.storylab.story_api.dto.UserResponse;
import dev.storylab.story_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
