package dev.storylab.userservice.service;

import dev.storylab.userservice.dto.CreateUserRequest;
import dev.storylab.userservice.dto.UserResponse;
import dev.storylab.userservice.entity.User;
import dev.storylab.userservice.exception.UserNotFoundException;
import dev.storylab.userservice.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if(userRepository.existsByAccountId(request.accountId())) {
            return getUserByAccountId(request.accountId());
        }

        log.info("Creating user profile for account {}", request.accountId());

        User user = new User();

        user.setAccountId(request.accountId());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public UserResponse getUserByAccountId(Long accountId) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(
                        () -> new UserNotFoundException(accountId)
                );

        return toResponse(user);
    }

    private UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getAccountId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
