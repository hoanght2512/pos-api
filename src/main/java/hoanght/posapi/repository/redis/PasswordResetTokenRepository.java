package hoanght.posapi.repository.redis;

import hoanght.posapi.model.redis.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, String> {
}
