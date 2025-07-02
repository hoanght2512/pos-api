package hoanght.posapi.controller.impl;

import hoanght.posapi.controller.IAuthController;
import hoanght.posapi.dto.request.ForgotPasswordRequest;
import hoanght.posapi.dto.request.LoginRequest;
import hoanght.posapi.dto.request.RegisterRequest;
import hoanght.posapi.dto.request.ResetPasswordRequest;
import hoanght.posapi.dto.response.AuthResponse;
import hoanght.posapi.dto.response.DataResponse;
import hoanght.posapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements IAuthController {
    private final AuthService AuthService;

    @Override
    public ResponseEntity<DataResponse<AuthResponse>> login(LoginRequest loginRequest, HttpServletResponse response) {
        return ResponseEntity.ok(DataResponse.success("Login successful", AuthService.login(loginRequest, response)));
    }

    @Override
    public ResponseEntity<DataResponse<Void>> register(RegisterRequest registerRequest) {
        AuthService.register(registerRequest);
        return ResponseEntity.ok(DataResponse.created("Registration successful", null));
    }

    @Override
    public ResponseEntity<DataResponse<AuthResponse>> refresh(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(DataResponse.success(AuthService.refreshToken(request, response)));
    }

    @Override
    public ResponseEntity<DataResponse<Void>> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        AuthService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(DataResponse.success("Reset password link has been sent to your email", null));
    }

    @Override
    public ResponseEntity<DataResponse<Void>> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        AuthService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(DataResponse.success("Password has been reset successfully", null));
    }

    @Override
    public ResponseEntity<DataResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        AuthService.logout(request, response);
        return ResponseEntity.ok(DataResponse.success("Logged out successfully", null));
    }
}
