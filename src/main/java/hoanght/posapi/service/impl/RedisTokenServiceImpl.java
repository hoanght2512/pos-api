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
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    public String createAndSaveRefreshToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("refresh_token:" + refreshToken, userId.toString(), refreshTokenExpirationMs, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    @Override
    public Optional<Long> getUserIdFromRefreshToken(String refreshToken) {
        Object userId = redisTemplate.opsForValue().get("refresh_token:" + refreshToken);
        if (userId instanceof Long) {
            return Optional.of((Long) userId);
        }
        return Optional.empty();
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("refresh_token:" + refreshToken);
    }

    @Override
    public String createAndSavePasswordResetToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("password_reset_token:" + token, userId.toString(), 15, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public Optional<Long> getUserIdFromPasswordResetToken(String token) {
        Object userId = redisTemplate.opsForValue().get("password_reset_token:" + token);
        if (userId instanceof Long) {
            return Optional.of((Long) userId);
        }
        return Optional.empty();
    }

    @Override
    public void deletePasswordResetToken(String token) {
        redisTemplate.delete("password_reset_token:" + token);
    }
}
