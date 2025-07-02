package hoanght.posapi.service.impl;

import hoanght.posapi.common.Role;
import hoanght.posapi.dto.EmailMessage;
import hoanght.posapi.dto.request.ForgotPasswordRequest;
import hoanght.posapi.dto.request.LoginRequest;
import hoanght.posapi.dto.request.RegisterRequest;
import hoanght.posapi.dto.request.ResetPasswordRequest;
import hoanght.posapi.dto.response.AuthResponse;
import hoanght.posapi.entity.User;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.ResourceNotFoundException;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.AuthService;
import hoanght.posapi.service.PasswordResetTokenService;
import hoanght.posapi.service.RefreshTokenService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TemplateEngine templateEngine;

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
    public AuthResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        return refreshTokenService.getAuthResponse(user, response);
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new BadRequestException("Username already exists");

        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new BadRequestException("Email already exists");

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFullName(registerRequest.getFullName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getFromCookie(request);
        UUID userId = refreshTokenService.getUserIdFromRefreshToken(refreshToken).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        refreshTokenService.deleteRefreshToken(refreshToken);

        return refreshTokenService.getAuthResponse(user, response);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        userRepository.findByEmailAndProvider(forgotPasswordRequest.getEmail(), "local").ifPresent(user -> {
            if (!user.isEnabled() || !user.isEmailVerified())
                throw new BadRequestException("User is locked or email not verified");
            String resetToken = passwordResetTokenService.generateToken(user);
            Context context = new Context();
            context.setVariable("name", user.getFullName());
            context.setVariable("resetUrl", String.format("%s/reset-password?token=%s", frontendUrl, resetToken));
            context.setVariable("appName", frontendName);
            context.setVariable("expirationTime", "15 phút");
            String htmlContent = templateEngine.process("email/reset-password", context);
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(user.getEmail()).subject(String.format("[%s] Yêu cầu đặt lại mật khẩu", frontendName)).body(htmlContent)
                    .build();
            rabbitTemplate.convertAndSend(emailExchangeName, emailRoutingKey, emailMessage);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        UUID userId = passwordResetTokenService.getUserIdByToken(resetPasswordRequest.getToken()).orElseThrow(() -> new BadRequestException("Invalid token"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Invalid token"));
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        passwordResetTokenService.deleteToken(resetPasswordRequest.getToken());
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getFromCookie(request);
        refreshTokenService.deleteRefreshToken(refreshToken);
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
