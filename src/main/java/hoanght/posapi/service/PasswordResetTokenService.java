package hoanght.posapi.service;

import hoanght.posapi.entity.User;

import java.util.Optional;

public interface PasswordResetTokenService {
    Optional<User> getUserByToken(String token);

    String generateToken(User user);

    void deleteToken(String token);
}
