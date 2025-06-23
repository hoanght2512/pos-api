package hoanght.posapi.service.impl;

import hoanght.posapi.common.Role;
import hoanght.posapi.dto.*;
import hoanght.posapi.entity.PasswordResetToken;
import hoanght.posapi.entity.RefreshToken;
import hoanght.posapi.entity.User;
import hoanght.posapi.repository.PasswordResetTokenRepository;
import hoanght.posapi.repository.RefreshTokenRepository;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.AuthService;
import hoanght.posapi.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;
    @Value("${frontend.url}")
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

        String accessToken = jwtProvider.generateToken(authentication);
        String refreshToken = UUID.randomUUID().toString();
        Instant expirationDate = Instant.now().plusMillis(refreshTokenExpiration);

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        RefreshToken newRefreshToken = RefreshToken.builder().token(refreshToken).user(user).expiryDate(expirationDate).revoked(false).build();

        refreshTokenRepository.save(newRefreshToken);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").username(user.getUsername()).fullName(user.getFullName()).userId(user.getId()).roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())).build();
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

        String accessToken = jwtProvider.generateToken(authentication);
        String refreshToken = UUID.randomUUID().toString();
        Instant expirationDate = Instant.now().plusMillis(refreshTokenExpiration);

        RefreshToken newRefreshToken = RefreshToken.builder().token(refreshToken).user(user).expiryDate(expirationDate).revoked(false).build();

        refreshTokenRepository.save(newRefreshToken);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").username(user.getUsername()).fullName(user.getFullName()).userId(user.getId()).roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())).build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken storedRefreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken()).orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (storedRefreshToken.isRevoked() || storedRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token is revoked or expired");
        }

        User user = storedRefreshToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtProvider.generateToken(authentication);

        refreshTokenRepository.delete(storedRefreshToken);

        String newRefreshToken = UUID.randomUUID().toString();
        Instant expirationDate = Instant.now().plusMillis(refreshTokenExpiration);

        RefreshToken newStoredRefreshToken = RefreshToken.builder().token(newRefreshToken).user(user).expiryDate(expirationDate).revoked(false).build();

        refreshTokenRepository.save(newStoredRefreshToken);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).tokenType("Bearer").username(user.getUsername()).fullName(user.getFullName()).userId(user.getId()).roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())).build();
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        userRepository.findByEmail(forgotPasswordRequest.getEmail()).ifPresent(user -> {
            passwordResetTokenRepository.findByUserAndUsedFalse(user).ifPresent(passwordResetTokenRepository::delete);

            String token = UUID.randomUUID().toString();
            Instant expiryDate = Instant.now().plusSeconds(3600);

            PasswordResetToken passwordResetToken = PasswordResetToken.builder().token(token).user(user).expiryDate(expiryDate).used(false).build();

            passwordResetTokenRepository.save(passwordResetToken);

            EmailMessage emailMessage = EmailMessage.builder().to(user.getEmail()).subject("Password Reset Request").body(String.format("To reset your password, please click the following link: %s/reset-password?token=%s", frontendUrl, token)).build();

            rabbitTemplate.convertAndSend(emailExchangeName, emailRoutingKey, emailMessage);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(resetPasswordRequest.getToken()).orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (passwordResetToken.isUsed() || passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Password reset token is invalid or expired");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        refreshTokenRepository.findByUserAndRevokedFalse(user).forEach(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken()).ifPresent(refreshTokenRepository::delete);
    }
}
