package hoanght.posapi.security;

import hoanght.posapi.common.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class CustomSecurityExpression {
    public boolean isAdminOrSelf(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }

        UUID authenticatedUserId = userDetails.getUser().getId();
        Set<Role> roles = userDetails.getUser().getRoles();

        return userId.equals(authenticatedUserId) || roles.contains(Role.ROLE_ADMIN);
    }
}
