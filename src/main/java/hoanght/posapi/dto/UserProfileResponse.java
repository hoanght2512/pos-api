package hoanght.posapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserProfileResponse {
    private String username;
    private String email;
    @JsonProperty("full_name")
    private String fullName;
}
