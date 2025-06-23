package hoanght.posapi.repository;

import hoanght.posapi.entity.PasswordResetToken;
import hoanght.posapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);
}