package hoanght.posapi.service.impl;

import hoanght.posapi.common.Role;
import hoanght.posapi.dto.AuthResponse;
import hoanght.posapi.dto.EmailMessage;
import hoanght.posapi.dto.user.UserLoginRequest;
import hoanght.posapi.dto.user.UserRegisterRequest;
import hoanght.posapi.dto.user.UserResetPasswordRequest;
import hoanght.posapi.entity.User;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.AuthService;
import hoanght.posapi.service.PasswordResetTokenService;
import hoanght.posapi.service.RefreshTokenService;
import hoanght.posapi.util.JwtProvider;
import jakarta.annotation.PostConstruct;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final PasswordResetTokenService passwordResetTokenService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TemplateEngine templateEngine;
    private final JwtProvider jwtProvider;

    @Value("${app.frontend.name}")
    private String frontendName;
    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${app.rabbitmq.email-exchange-name}")
    private String emailExchangeName;
    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

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
    public AuthResponse login(UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        String refreshToken = refreshTokenService.createAndSaveRefreshToken(user.getId().toString());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").build();
    }

    @Override
    @Transactional
    public AuthResponse register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByUsername(userRegisterRequest.getUsername()))
            throw new AlreadyExistsException("Username already exists");

        if (userRepository.existsByEmail(userRegisterRequest.getEmail()))
            throw new AlreadyExistsException("Email already exists");

        User user = new User();
        user.setUsername(userRegisterRequest.getUsername());
        user.setEmail(userRegisterRequest.getEmail());
        user.setFullName(userRegisterRequest.getFullName());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));

        userRepository.save(user);
        String refreshToken = refreshTokenService.createAndSaveRefreshToken(user.getId().toString());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").build();
    }

    @Override
    @Transactional
    public AuthResponse refresh(String token) {
        UUID userId = refreshTokenService.getUserIdFromRefreshToken(token).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        refreshTokenService.deleteRefreshToken(token);

        String newRefreshToken = refreshTokenService.createAndSaveRefreshToken(user.getId().toString());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).tokenType("Bearer").build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        refreshTokenService.deleteRefreshToken(token);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmailAndProvider(email, "local").ifPresent(user -> {
            if (!user.isEnabled() || !user.isEmailVerified())
                throw new BadRequestException("User is locked or email not verified");
            String resetToken = passwordResetTokenService.createAndSaveToken(user.getId().toString());
            Context context = new Context();
            context.setVariable("name", user.getFullName());
            context.setVariable("resetUrl", String.format("%s/reset-password?token=%s", frontendUrl, resetToken));
            context.setVariable("appName", frontendName);
            context.setVariable("expirationTime", "15 phút");
            String htmlContent = templateEngine.process("email/reset-password", context);
            EmailMessage emailMessage = EmailMessage.builder().to(user.getEmail()).subject(String.format("[%s] Yêu cầu đặt lại mật khẩu", frontendName)).body(htmlContent).build();
            rabbitTemplate.convertAndSend(emailExchangeName, emailRoutingKey, emailMessage);
        });
    }

    @Override
    @Transactional
    public void resetPassword(UserResetPasswordRequest userResetPasswordRequest) {
        UUID userId = passwordResetTokenService.getUserIdByToken(userResetPasswordRequest.getToken()).orElseThrow(() -> new BadRequestException("Invalid reset token"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Invalid reset token"));
        user.setPassword(passwordEncoder.encode(userResetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        passwordResetTokenService.deleteToken(userResetPasswordRequest.getToken());
    }
}
