package hoanght.posapi.service.impl;

import hoanght.posapi.entity.User;
import hoanght.posapi.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<User> getUserByToken(String token) {
        String userId = (String) redisTemplate.opsForValue().get("password_reset_token:" + token);
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(UUID.fromString(userId));
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
