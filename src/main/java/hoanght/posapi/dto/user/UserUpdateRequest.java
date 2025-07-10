package hoanght.posapi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.common.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateRequest {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("email")
    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    @JsonProperty("roles")
    private Set<Role> roles;
}
