package hoanght.posapi.service.impl;

import hoanght.posapi.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    public String createAndSaveRefreshToken(String userId) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("refresh_token:" + refreshToken, userId, refreshTokenExpirationMs, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    @Override
    public Optional<UUID> getUserIdFromRefreshToken(String refreshToken) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("refresh_token:" + refreshToken)).map(UUID::fromString);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("refresh_token:" + refreshToken);
    }

    @Override
    public String createAndSavePasswordResetToken(String userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("password_reset_token:" + token, userId, 15, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public Optional<UUID> getUserIdFromPasswordResetToken(String token) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("password_reset_token:" + token)).map(UUID::fromString);
    }

    @Override
    public void deletePasswordResetToken(String token) {
        redisTemplate.delete("password_reset_token:" + token);
    }
}
