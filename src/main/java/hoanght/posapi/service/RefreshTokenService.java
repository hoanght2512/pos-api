package hoanght.posapi.service;

import hoanght.posapi.dto.response.AuthResponse;
import hoanght.posapi.entity.User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    AuthResponse getAuthResponse(User user, HttpServletResponse response);

    Optional<UUID> getUserIdFromRefreshToken(String refreshToken);
    void deleteRefreshToken(String refreshToken);
}
