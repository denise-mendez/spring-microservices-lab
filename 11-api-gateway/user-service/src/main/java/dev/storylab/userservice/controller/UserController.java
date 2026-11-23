package dev.storylab.userservice.controller;

import dev.storylab.userservice.dto.CreateUserRequest;
import dev.storylab.userservice.dto.UserResponse;
import dev.storylab.userservice.entity.User;
import dev.storylab.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping("/accounts/{accountId}")
    public UserResponse getUserByAccountId(@PathVariable Long accountId){
        return userService.getUserByAccountId(accountId);
    }
}
