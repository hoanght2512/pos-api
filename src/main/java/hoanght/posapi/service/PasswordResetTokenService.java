package hoanght.posapi.service;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenService {
    Optional<UUID> getUserIdByToken(String token);

    String createAndSaveToken(String userId);

    void deleteToken(String token);
}
