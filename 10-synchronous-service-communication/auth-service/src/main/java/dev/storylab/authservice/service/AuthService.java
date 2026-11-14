package dev.storylab.authservice.service;

import dev.storylab.authservice.dto.*;
import dev.storylab.authservice.entity.Account;
import dev.storylab.authservice.exception.AccountNotFoundException;
import dev.storylab.authservice.exception.UsernameAlreadyExistsException;
import dev.storylab.authservice.repository.AccountRepository;
import dev.storylab.authservice.security.JwtService;
import dev.storylab.authservice.security.SecurityRole;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    AuthenticationManager authenticationManager;

    public AuthService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AccountResponse register(RegisterRequest request){
        if (accountRepository.existsByUsername(request.username()))
            throw new UsernameAlreadyExistsException(request.username());

        Account account = new Account();

        account.setUsername(request.username());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setRole(SecurityRole.USER);
        account.setCreatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);

        return new AccountResponse(
                savedAccount.getId(),
                savedAccount.getUsername(),
                savedAccount.getRole(),
                savedAccount.getCreatedAt()
        );
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        Account account = accountRepository.findByUsername(request.username())
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "User " + request.username() + " was not found."
                        )
                );
        String token = jwtService.generateToken(account);

        return new AuthResponse(
                token,
                "Bearer",
                account.getId(),
                account.getUsername(),
                account.getRole()
        );
    }
}
