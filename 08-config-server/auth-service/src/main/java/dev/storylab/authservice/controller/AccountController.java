package dev.storylab.authservice.controller;

import dev.storylab.authservice.dto.AccountResponse;
import dev.storylab.authservice.dto.UpdateRoleRequest;
import dev.storylab.authservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //Temporary for manual role testing.
    //To be secured as ADMIN only
    @PatchMapping("/{id}/role")
    public AccountResponse updateRole(
            @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request
    ) {
        return accountService.updateRole(id, request.role());
    }
}
