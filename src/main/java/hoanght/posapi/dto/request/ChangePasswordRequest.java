package hoanght.posapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @JsonProperty("old_password")
    @NotBlank(message = "Old password must not be blank")
    private String oldPassword;
    @JsonProperty("new_password")
    @NotBlank(message = "New password must not be blank")
    private String newPassword;
}
