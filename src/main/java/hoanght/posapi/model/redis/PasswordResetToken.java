package hoanght.posapi.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("password_reset_token")
public class PasswordResetToken {
    @Id
    private String id;

    private Long userId;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiresIn;
}
