package hoanght.posapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @JsonProperty("full_name")
    @NotBlank(message = "Full name cannot be blank")
    private String fullName;
    // Tách phần email ra cập nhật riêng để xác thực email
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;
}
