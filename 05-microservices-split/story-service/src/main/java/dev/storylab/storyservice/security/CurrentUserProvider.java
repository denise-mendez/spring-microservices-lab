package dev.storylab.storyservice.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserProvider {
    public Optional<CurrentUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null || !authentication.isAuthenticated()
        || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            return Optional.empty();
        }

        return Optional.of(currentUser);
    }

    public CurrentUser getRequiredUser() {
        return getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authentication is required"));
    }
}
