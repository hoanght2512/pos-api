package hoanght.posapi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResponse extends RepresentationModel<UserResponse> {
    @JsonProperty("id")
    private Long id;

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
