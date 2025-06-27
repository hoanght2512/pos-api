package hoanght.posapi.service;

import hoanght.posapi.dto.AuthResponse;
import hoanght.posapi.entity.User;
import org.springframework.security.core.Authentication;

public interface RefreshTokenService {
    AuthResponse getAuthResponse(User user, Authentication authentication);
    String getUserIdFromRefreshToken(String refreshToken);
    void deleteRefreshToken(String refreshToken);
}
