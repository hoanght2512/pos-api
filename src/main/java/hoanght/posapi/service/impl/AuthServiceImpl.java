package hoanght.posapi.service.impl;

import hoanght.posapi.common.Role;
import hoanght.posapi.dto.auth.AuthResponse;
import hoanght.posapi.dto.auth.LoginRequest;
import hoanght.posapi.dto.auth.RegisterRequest;
import hoanght.posapi.dto.auth.ResetPasswordRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.User;
import hoanght.posapi.repository.jpa.UserRepository;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.AuthService;
import hoanght.posapi.service.EmailService;
import hoanght.posapi.service.RedisTokenService;
import hoanght.posapi.util.JwtProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final RedisTokenService redisTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;

    @Value("${app.expiration-ms}")
    private Long expirationMs;

    @PostConstruct
    public void init() {
        userRepository.findByUsername("admin").orElseGet(() -> {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFullName("Administrator");
            adminUser.setEnabled(true);
            adminUser.setRoles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));

            return userRepository.save(adminUser);
        });
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        String refreshToken = redisTokenService.createAndSaveRefreshToken(user.getId());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expirationMs / 1000)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new AlreadyExistsException("Username already exists");

        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new AlreadyExistsException("Email already exists");

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFullName(registerRequest.getFullName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);
        String refreshToken = redisTokenService.createAndSaveRefreshToken(user.getId());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expirationMs / 1000)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refresh(String token) {
        Long userId = redisTokenService.getUserIdFromRefreshToken(token).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        redisTokenService.deleteRefreshToken(token);

        String newRefreshToken = redisTokenService.createAndSaveRefreshToken(user.getId());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(expirationMs / 1000)
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        redisTokenService.deleteRefreshToken(token);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmailAndProvider(email, "local").ifPresent(user -> {
            if (!user.isEnabled() || !user.isEmailVerified())
                throw new BadRequestException("User is locked or email not verified");
            String resetToken = redisTokenService.createAndSavePasswordResetToken(user.getId());
            emailService.sendEmailResetPassword(email, user.getFullName(), resetToken);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Long userId = redisTokenService.getUserIdFromPasswordResetToken(resetPasswordRequest.getToken()).orElseThrow(() -> new BadRequestException("Invalid reset token"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Invalid reset token"));
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        redisTokenService.deletePasswordResetToken(resetPasswordRequest.getToken());
    }
}
