package hoanght.posapi.service.impl;

import hoanght.posapi.common.Role;
import hoanght.posapi.dto.*;
import hoanght.posapi.entity.User;
import hoanght.posapi.exception.ResourceNotFoundException;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.AuthService;
import hoanght.posapi.service.PasswordResetTokenService;
import hoanght.posapi.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${app.rabbitmq.email-exchange-name}")
    private String emailExchangeName;
    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        return refreshTokenService.getAuthResponse(user, authentication);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent())
            throw new IllegalArgumentException("Username already exists");

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already exists");

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFullName(registerRequest.getFullName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return refreshTokenService.getAuthResponse(user, authentication);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userId = refreshTokenService.getUserIdFromRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        return refreshTokenService.getAuthResponse(user, authentication);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        userRepository.findByEmail(forgotPasswordRequest.getEmail()).ifPresent(user -> {
            String resetToken = passwordResetTokenService.generateToken(user);
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(user.getEmail())
                    .subject("Password Reset Request")
                    .body(String.format("To reset your password, please click the following link: %s/reset-password?token=%s", frontendUrl, resetToken))
                    .build();
            rabbitTemplate.convertAndSend(emailExchangeName, emailRoutingKey, emailMessage);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = passwordResetTokenService.getUserByToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired password reset token"));
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        passwordResetTokenService.deleteToken(resetPasswordRequest.getToken());
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
    }
}
