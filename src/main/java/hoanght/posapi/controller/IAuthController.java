package hoanght.posapi.controller;

import hoanght.posapi.dto.request.ForgotPasswordRequest;
import hoanght.posapi.dto.request.LoginRequest;
import hoanght.posapi.dto.request.RegisterRequest;
import hoanght.posapi.dto.request.ResetPasswordRequest;
import hoanght.posapi.dto.response.AuthResponse;
import hoanght.posapi.dto.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public interface IAuthController {
    @Operation(summary = "Login", description = "Authenticate user and return access and refresh tokens")
    @PostMapping("/login")
    ResponseEntity<DataResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response);

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user")
    ResponseEntity<DataResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest);

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    ResponseEntity<DataResponse<AuthResponse>> refresh(HttpServletRequest request, HttpServletResponse response);

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password", description = "Send reset password link to user's email")
    ResponseEntity<DataResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest);

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Reset user's password using the reset link")
    ResponseEntity<DataResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest);

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user and invalidate tokens")
    ResponseEntity<DataResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response);
}
