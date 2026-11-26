package dev.storylab.authservice.service;

import dev.storylab.authservice.client.UserClient;
import dev.storylab.authservice.client.dto.CreateUserRequest;
import dev.storylab.authservice.dto.*;
import dev.storylab.authservice.entity.Account;
import dev.storylab.authservice.exception.UsernameAlreadyExistsException;
import dev.storylab.authservice.repository.AccountRepository;
import dev.storylab.authservice.security.JwtService;
import dev.storylab.authservice.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final UserClient userClient;

    AuthenticationManager authenticationManager;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService, UserClient userClient) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userClient = userClient;
    }

    public AccountResponse register(RegisterRequest request){
        if (accountRepository.existsByUsername(request.username()))
            throw new UsernameAlreadyExistsException(request.username());

        log.info("Registering account for username {}", request.username());

        Account account = new Account();

        account.setUsername(request.username());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setRole(SecurityRole.USER);
        account.setCreatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);

        log.info("Provisioning user profile for account {}", savedAccount.getId());

        userClient.createUser(new CreateUserRequest(
                savedAccount.getId(),
                savedAccount.getUsername(),
                request.email()
        ));

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
