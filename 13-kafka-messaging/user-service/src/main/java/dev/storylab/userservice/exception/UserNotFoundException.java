package dev.storylab.userservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long accountId) {
        super("User with account id " + accountId + " not found");
    }
}
