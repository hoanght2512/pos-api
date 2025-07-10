package hoanght.posapi.repository;

import hoanght.posapi.entity.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

    Optional<User> findByEmailAndProvider(String email, String provider);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
