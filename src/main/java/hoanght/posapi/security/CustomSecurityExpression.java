package hoanght.posapi.security;

import hoanght.posapi.common.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CustomSecurityExpression {
    public boolean isAdminOrSelf(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }

        Long authenticatedUserId = userDetails.getUser().getId();
        Set<Role> roles = userDetails.getUser().getRoles();

        return userId.equals(authenticatedUserId) || roles.contains(Role.ROLE_ADMIN);
    }
}
