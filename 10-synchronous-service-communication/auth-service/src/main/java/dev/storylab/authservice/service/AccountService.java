package dev.storylab.authservice.service;

import dev.storylab.authservice.dto.AccountResponse;
import dev.storylab.authservice.entity.Account;
import dev.storylab.authservice.exception.AccountNotFoundException;
import dev.storylab.authservice.repository.AccountRepository;
import dev.storylab.authservice.security.SecurityRole;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse updateRole(Long id, SecurityRole newRole){
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException(id)
        );

        account.setRole(newRole);

        Account savedAccount = accountRepository.save(account);

        return new AccountResponse(
                savedAccount.getId(),
                savedAccount.getUsername(),
                savedAccount.getRole(),
                savedAccount.getCreatedAt()
        );
    }
}
