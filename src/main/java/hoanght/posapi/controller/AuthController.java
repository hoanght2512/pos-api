package hoanght.posapi.controller;

import hoanght.posapi.dto.AuthResponse;
import hoanght.posapi.dto.DataResponse;
import hoanght.posapi.dto.user.UserLoginRequest;
import hoanght.posapi.dto.user.UserRegisterRequest;
import hoanght.posapi.dto.user.UserResetPasswordRequest;
import hoanght.posapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public class AuthController {
    private final AuthService AuthService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return access and refresh tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<DataResponse<AuthResponse>> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        return ResponseEntity.ok(DataResponse.success("Login successful", AuthService.login(userLoginRequest)));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<DataResponse<AuthResponse>> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        return ResponseEntity.ok(DataResponse.success("Registration successful", AuthService.register(userRegisterRequest)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<DataResponse<AuthResponse>> refresh(@RequestParam("token") String token) {
        return ResponseEntity.ok(DataResponse.success("Token refreshed successfully", AuthService.refresh(token)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user and invalidate tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid reset token")
    })
    public ResponseEntity<DataResponse<Void>> logout(@RequestParam(value = "token") String token) {
        AuthService.logout(token);
        return ResponseEntity.ok(DataResponse.success("Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password", description = "Send reset password link to user's email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset password link sent successfully"),
            @ApiResponse(responseCode = "400", description = "Account is locked or not verified")
    })
    public ResponseEntity<DataResponse<Void>> forgotPassword(@RequestParam String email) {
        AuthService.forgotPassword(email);
        return ResponseEntity.ok(DataResponse.success("Reset password link sent successfully"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Reset user's password using the reset link")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    public ResponseEntity<DataResponse<Void>> resetPassword(@Valid @RequestBody UserResetPasswordRequest userResetPasswordRequest) {
        AuthService.resetPassword(userResetPasswordRequest);
        return ResponseEntity.ok(DataResponse.success("Password reset successfully"));
    }
}
