package hoanght.posapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String email;
    @JsonProperty("full_name")
    private String fullName;
    private Set<String> roles;
}
