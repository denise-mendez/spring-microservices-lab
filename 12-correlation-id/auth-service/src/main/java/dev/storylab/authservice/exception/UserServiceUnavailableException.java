package dev.storylab.authservice.exception;

public class UserServiceUnavailableException extends RuntimeException {

    public UserServiceUnavailableException() {
        super("User Service is currently unavailable");
    }

    public UserServiceUnavailableException(Throwable message) {
        super(message);
    }
}
