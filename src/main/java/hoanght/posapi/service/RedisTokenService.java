package hoanght.posapi.service;

import java.util.Optional;

public interface RedisTokenService {
    String createAndSaveRefreshToken(Long userId);

    Optional<Long> getUserIdFromRefreshToken(String refreshToken);

    void deleteRefreshToken(String refreshToken);

    String createAndSavePasswordResetToken(Long userId);

    Optional<Long> getUserIdFromPasswordResetToken(String token);

    void deletePasswordResetToken(String token);
}
