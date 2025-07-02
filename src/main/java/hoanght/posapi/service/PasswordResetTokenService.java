package hoanght.posapi.service;

import hoanght.posapi.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenService {
    Optional<UUID> getUserIdByToken(String token);

    String generateToken(User user);

    void deleteToken(String token);
}
