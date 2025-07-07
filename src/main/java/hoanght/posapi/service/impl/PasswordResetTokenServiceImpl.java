package hoanght.posapi.service.impl;

import hoanght.posapi.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Optional<UUID> getUserIdByToken(String token) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("password_reset_token:" + token)).map(UUID::fromString);
    }

    @Override
    public String createAndSaveToken(String userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("password_reset_token:" + token, userId, 15, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete("password_reset_token:" + token);
    }
}
