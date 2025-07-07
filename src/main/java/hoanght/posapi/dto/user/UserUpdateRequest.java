package hoanght.posapi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.common.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateRequest {
    @JsonProperty("username")
    private String username;

    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    @JsonProperty("password")
    private String password;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("roles")
    private Set<Role> roles;
}
