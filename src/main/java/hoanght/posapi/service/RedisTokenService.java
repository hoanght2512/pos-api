package hoanght.posapi.service;

import java.util.Optional;
import java.util.UUID;

public interface RedisTokenService {
    String createAndSaveRefreshToken(String userId);

    Optional<UUID> getUserIdFromRefreshToken(String refreshToken);

    void deleteRefreshToken(String refreshToken);

    String createAndSavePasswordResetToken(String userId);

    Optional<UUID> getUserIdFromPasswordResetToken(String token);

    void deletePasswordResetToken(String token);
}
