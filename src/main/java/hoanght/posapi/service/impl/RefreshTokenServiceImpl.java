package hoanght.posapi.service.impl;

import hoanght.posapi.dto.AuthResponse;
import hoanght.posapi.entity.User;
import hoanght.posapi.service.RefreshTokenService;
import hoanght.posapi.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Override
    public AuthResponse getAuthResponse(User user, Authentication authentication) {
        String accessToken = jwtProvider.generateToken(authentication);
        String refreshToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set("refresh_token:" + refreshToken, user.getId(), refreshTokenExpiration, TimeUnit.MILLISECONDS);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .fullName(user.getFullName())
                .userId(user.getId())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public String getUserIdFromRefreshToken(String refreshToken) {
        return (String) redisTemplate.opsForValue().get("refresh_token:" + refreshToken);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("refresh_token:" + refreshToken);
    }
}
