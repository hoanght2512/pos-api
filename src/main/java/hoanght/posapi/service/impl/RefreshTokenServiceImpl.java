package hoanght.posapi.service.impl;

import hoanght.posapi.dto.response.AuthResponse;
import hoanght.posapi.entity.User;
import hoanght.posapi.service.RefreshTokenService;
import hoanght.posapi.util.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public AuthResponse getAuthResponse(User user, HttpServletResponse response) {
        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set("refresh_token:" + refreshToken, user.getId(), 7, TimeUnit.DAYS);
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(7));
        response.addCookie(refreshTokenCookie);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .fullName(user.getFullName())
                .userId(user.getId())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public Optional<UUID> getUserIdFromRefreshToken(String refreshToken) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("refresh_token:" + refreshToken))
                .map(Object::toString)
                .map(UUID::fromString);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("refresh_token:" + refreshToken);
    }
}
