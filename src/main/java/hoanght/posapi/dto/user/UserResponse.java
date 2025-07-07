package hoanght.posapi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;

    private String username;

    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;

    @JsonProperty("is_enabled")
    private boolean isEnabled;

    private Set<String> roles;
}
