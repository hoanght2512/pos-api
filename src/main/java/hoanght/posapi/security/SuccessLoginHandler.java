package hoanght.posapi.security;

import hoanght.posapi.entity.User;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.util.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuccessLoginHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            try {
                User user = toUser(token);
                getRedirectStrategy().sendRedirect(request, response, getTargetUrl(user, response));
            } catch (Exception e) {
                getRedirectStrategy().sendRedirect(request, response, redirectUri + "?error=" + e.getMessage());
            }
        }
    }

    private String generateUsername(String username) {
        username = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        username = username.split("@")[0];
        int count = 0;
        String baseUsername = username;
        while (userRepository.existsByUsername(username)) {
            count++;
            username = baseUsername + "_" + count;
        }
        return username;
    }

    private String getTargetUrl(User user, HttpServletResponse response) {
        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("refresh_token:" + refreshToken, user.getId(), 7, TimeUnit.DAYS);
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(7));
        response.addCookie(cookie);
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("access_token", accessToken)
                .build().toString();
    }

    private User toUser(OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        String providerId = token.getPrincipal().getAttribute(provider.equals("github") ? "id" : "sub").toString();

        return userRepository.findByProviderAndProviderId(provider, providerId).orElseGet(() -> {
            String fullName = (String) Optional.ofNullable(token.getPrincipal().getAttribute("name")).orElse("NOT_FOUND");
            String avatarUrl = token.getPrincipal().getAttribute(provider.equals("github") ? "avatar_url" : "picture");
            String username = token.getPrincipal().getAttribute(provider.equals("github") ? "login" : "email");
            String email = token.getPrincipal().getAttribute("email");

            if (userRepository.existsByEmail(email) && email != null)
                throw new BadRequestException("Email already exists");

            username = generateUsername(username);

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setAvatarUrl(avatarUrl);
            user.setProvider(provider);
            user.setProviderId(providerId);

            return userRepository.save(user);
        });
    }
}
