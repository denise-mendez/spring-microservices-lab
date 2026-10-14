package dev.storylab.story_api.service;

import dev.storylab.story_api.dto.RegisterRequest;
import dev.storylab.story_api.dto.UserResponse;
import dev.storylab.story_api.entity.User;
import dev.storylab.story_api.exception.UsernameAlreadyExistsException;
import dev.storylab.story_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import dev.storylab.story_api.entity.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
