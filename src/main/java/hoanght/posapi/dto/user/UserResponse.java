package hoanght.posapi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserResponse {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;

    @JsonProperty("is_enabled")
    private boolean isEnabled;

    @JsonProperty("roles")
    private Set<String> roles;
}
