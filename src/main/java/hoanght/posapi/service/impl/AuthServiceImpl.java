package hoanght.posapi.service.impl;

import hoanght.posapi.common.Role;
import hoanght.posapi.dto.auth.AuthResponse;
import hoanght.posapi.dto.common.EmailMessage;
import hoanght.posapi.dto.auth.LoginRequest;
import hoanght.posapi.dto.auth.RegisterRequest;
import hoanght.posapi.dto.auth.ResetPasswordRequest;
import hoanght.posapi.entity.User;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.AuthService;
import hoanght.posapi.service.RedisTokenService;
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
    private final AuthenticationManager authenticationManager;
    private final RedisTokenService redisTokenService;
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
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        String refreshToken = redisTokenService.createAndSaveRefreshToken(user.getId().toString());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").build();
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
        String refreshToken = redisTokenService.createAndSaveRefreshToken(user.getId().toString());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").build();
    }

    @Override
    @Transactional
    public AuthResponse refresh(String token) {
        UUID userId = redisTokenService.getUserIdFromRefreshToken(token).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        redisTokenService.deleteRefreshToken(token);

        String newRefreshToken = redisTokenService.createAndSaveRefreshToken(user.getId().toString());
        String accessToken = jwtProvider.generateToken(user);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).tokenType("Bearer").build();
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
            String resetToken = redisTokenService.createAndSavePasswordResetToken(user.getId().toString());
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
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        UUID userId = redisTokenService.getUserIdFromPasswordResetToken(resetPasswordRequest.getToken()).orElseThrow(() -> new BadRequestException("Invalid reset token"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Invalid reset token"));
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        redisTokenService.deletePasswordResetToken(resetPasswordRequest.getToken());
    }
}
