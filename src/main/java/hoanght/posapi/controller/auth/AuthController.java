package hoanght.posapi.controller.auth;

import hoanght.posapi.dto.auth.AuthResponse;
import hoanght.posapi.dto.auth.LoginRequest;
import hoanght.posapi.dto.auth.ResetPasswordRequest;
import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<DataResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(DataResponse.success("Login successful", authService.login(loginRequest)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<DataResponse<AuthResponse>> refresh(@RequestParam("token") String token) {
        return ResponseEntity.ok(DataResponse.success("Token refreshed successfully", authService.refresh(token)));
    }

    @PostMapping("/logout")
    public ResponseEntity<DataResponse<Void>> logout(@RequestParam(value = "token") String token) {
        authService.logout(token);
        return ResponseEntity.ok(DataResponse.success("Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<DataResponse<Void>> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(DataResponse.success("Reset password link sent successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<DataResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(DataResponse.success("Password reset successfully"));
    }
}
