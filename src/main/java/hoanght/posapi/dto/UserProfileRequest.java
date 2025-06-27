package hoanght.posapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileRequest {
    @JsonProperty("full_name")
    @NotBlank(message = "Full name is required")
    private String fullName;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
}
