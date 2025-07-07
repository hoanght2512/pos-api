package hoanght.posapi.service.impl;

import hoanght.posapi.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
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
}
