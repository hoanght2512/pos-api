package hoanght.posapi.service.impl;

import hoanght.posapi.entity.User;
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
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<UUID> getUserIdByToken(String token) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("password_reset_token:" + token))
                .map(Object::toString)
                .map(UUID::fromString);
    }

    @Override
    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("password_reset_token:" + token, user.getId(), 15, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete("password_reset_token:" + token);
    }
}
