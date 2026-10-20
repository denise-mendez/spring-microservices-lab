package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.LoginRequest;
import dev.storylab.story_api.dto.RegisterRequest;
import dev.storylab.story_api.dto.UserResponse;
import dev.storylab.story_api.entity.User;
import dev.storylab.story_api.exception.UsernameAlreadyExistsException;
import dev.storylab.story_api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import dev.storylab.story_api.entity.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public UserResponse register(RegisterRequest request){
        if (userRepository.existsByUsername(request.username()))
            throw new UsernameAlreadyExistsException(request.username());

        User user = new User();

        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    public User authenticate(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        return userRepository.findByUsername(request.username())
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "User " + request.username() + " was not found."
                        )
                );
    }
}
