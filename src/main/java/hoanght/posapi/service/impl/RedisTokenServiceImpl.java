package hoanght.posapi.service.impl;

import hoanght.posapi.model.redis.PasswordResetToken;
import hoanght.posapi.model.redis.RefreshToken;
import hoanght.posapi.repository.redis.PasswordResetTokenRepository;
import hoanght.posapi.repository.redis.RefreshTokenRepository;
import hoanght.posapi.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    public String createAndSaveRefreshToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenRepository.save(new RefreshToken(refreshToken, userId, refreshTokenExpirationMs));
        return refreshToken;
    }

    @Override
    public Optional<Long> getUserIdFromRefreshToken(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken)
                .map(RefreshToken::getUserId);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    @Override
    public String createAndSavePasswordResetToken(Long userId) {
        String token = UUID.randomUUID().toString();
        passwordResetTokenRepository.save(new PasswordResetToken(token, userId, 15L * 60 * 1000));
        return token;
    }

    @Override
    public Optional<Long> getUserIdFromPasswordResetToken(String token) {
        return passwordResetTokenRepository.findById(token)
                .map(PasswordResetToken::getUserId);
    }

    @Override
    public void deletePasswordResetToken(String token) {
        passwordResetTokenRepository.deleteById(token);
    }
}
