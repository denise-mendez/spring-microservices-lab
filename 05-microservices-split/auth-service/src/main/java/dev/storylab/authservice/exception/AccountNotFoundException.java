package dev.storylab.authservice.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Account with id " + id + " was not found");
    }
}
