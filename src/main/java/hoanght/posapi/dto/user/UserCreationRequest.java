package hoanght.posapi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.common.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserCreationRequest {
    @NotBlank
    @Size(min = 6, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9._-]{6,20}$", message = "Username must be 6-20 characters long and can contain letters, numbers, dots, underscores, and hyphens")
    private String username;
    @NotBlank
    @Size(min = 8, max = 100)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password;
    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Full name can only contain letters and spaces")
    @JsonProperty("full_name")
    private String fullName;
    @Email
    @Size(max = 100)
    private String email;
    private Set<Role> roles = new HashSet<>();
}
