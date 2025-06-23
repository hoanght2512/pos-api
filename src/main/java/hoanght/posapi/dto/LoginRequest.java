package hoanght.posapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest implements Serializable {
    @NotBlank(message = "Username or email is required")
    String username;
    @NotBlank(message = "Password is required")
    String password;
}