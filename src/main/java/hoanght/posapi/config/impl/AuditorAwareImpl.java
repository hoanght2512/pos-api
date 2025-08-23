package hoanght.posapi.config.impl;

import hoanght.posapi.security.CustomUserDetails;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return Optional.empty();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return Optional.ofNullable(userDetails.getUser().getId());
    }
}
