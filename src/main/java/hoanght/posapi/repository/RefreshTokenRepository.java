package hoanght.posapi.repository;

import hoanght.posapi.entity.RefreshToken;
import hoanght.posapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    Iterable<RefreshToken> findByUserAndRevokedFalse(User user);
}