package hoanght.posapi.service;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    String createAndSaveRefreshToken(String userId);

    Optional<UUID> getUserIdFromRefreshToken(String refreshToken);

    void deleteRefreshToken(String refreshToken);
}
