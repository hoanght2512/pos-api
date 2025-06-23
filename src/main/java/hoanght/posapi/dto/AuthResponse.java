package hoanght.posapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("user_id")
    private UUID userId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("roles")
    private Set<String> roles;
}
