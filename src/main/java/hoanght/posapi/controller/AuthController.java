package hoanght.posapi.controller;

import hoanght.posapi.dto.*;
import hoanght.posapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<DataResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(DataResponse.success(authService.login(loginRequest)));
    }

    @PostMapping("/register")
    public ResponseEntity<DataResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(DataResponse.created(authService.register(registerRequest)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<DataResponse<AuthResponse>> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(DataResponse.success(authService.refreshToken(refreshTokenRequest)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<DataResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(DataResponse.success("Reset password link has been sent to your email", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<DataResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(DataResponse.success("Password has been reset successfully", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<DataResponse<Void>> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.logout(refreshTokenRequest);
        return ResponseEntity.ok(DataResponse.success("Logged out successfully", null));
    }
}
