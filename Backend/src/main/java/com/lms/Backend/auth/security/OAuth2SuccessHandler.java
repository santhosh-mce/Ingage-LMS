package com.lms.Backend.auth.security;

import com.lms.Backend.user.entity.AuthProvider;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final String frontendSuccessUrl;

    public OAuth2SuccessHandler(
        UserRepository userRepository,
        JwtService jwtService,
        @Value("${app.frontend-oauth-success-url}") String frontendSuccessUrl
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.frontendSuccessUrl = normalizeSuccessUrl(frontendSuccessUrl);
    }

    private static String normalizeSuccessUrl(String url) {
        if (url == null || url.isBlank()) {
            return "https://ingage-lms.vercel.app/oauth/callback";
        }
        String trimmed = url.trim();
        if (trimmed.contains("/oauth/callback")) {
            return trimmed.replaceAll("/+$", "");
        }
        return trimmed.replaceAll("/+$", "") + "/oauth/callback";
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
            OAuth2User oauthUser = oauthToken.getPrincipal();
            Map<String, Object> attributes = oauthUser.getAttributes();

            log.info("[OAuth2-Callback] Received authentication callback from provider: {}", registrationId);
            log.debug("[OAuth2-Callback] Provider attributes: {}", attributes.keySet());

            AuthProvider provider = providerFor(registrationId);
            String email = extractEmail(attributes);
            String providerId = String.valueOf(attributes.getOrDefault("sub", oauthUser.getName()));

            Object nameObj = attributes.get("name");
            String name = (nameObj != null && !nameObj.toString().isBlank())
                ? nameObj.toString().trim()
                : email.substring(0, email.indexOf('@'));

            Object pictureObj = attributes.get("picture");
            if (pictureObj == null) {
                pictureObj = attributes.get("avatar_url");
            }
            String profileImage = pictureObj != null ? pictureObj.toString() : null;

            log.info("[OAuth2-Callback] Finding/creating user in database for email: {}, provider: {}", email, provider);
            Optional<User> existingUserOpt = userRepository.findByProviderAndProviderId(provider, providerId);
            if (existingUserOpt.isEmpty()) {
                existingUserOpt = userRepository.findByEmailIgnoreCase(email);
            }

            User user;
            if (existingUserOpt.isPresent()) {
                user = existingUserOpt.get();
                log.info("[OAuth2-Callback] Existing user found: id={}, email={}. Preserving existing profile_image: {}",
                    user.getId(), user.getEmail(), user.getProfileImage());

                // Update Google OAuth related information if required
                user.setProvider(provider);
                user.setProviderId(providerId);
                user.setEmailVerified(true);
                user.setLastLogin(Instant.now());

                if (name != null && !name.isBlank()) {
                    user.setName(name);
                }

                if (user.getRole() == null) {
                    user.setRole(UserRole.STUDENT);
                }

                // CRITICAL RULE:
                // Google OAuth login must NEVER overwrite, clear, or replace an existing profile_image!
                // If profile_image already exists in PostgreSQL (e.g. /uploads/profile-images/...),
                // it MUST remain exactly the same.
                // Do NOT call user.setProfileImage(profileImage) or user.setProfileImage(null) on existing user!
            } else {
                log.info("[OAuth2-Callback] New OAuth user: email={}, provider={}", email, provider);
                user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setProvider(provider);
                user.setProviderId(providerId);
                user.setProfileImage(profileImage);
                user.setEmailVerified(true);
                user.setRole(UserRole.STUDENT);
                user.setLastLogin(Instant.now());
            }

            user = userRepository.save(user);
            log.info("[OAuth2-Callback] Database user saved: id={}, email={}, role={}, profileImage={}",
                user.getId(), user.getEmail(), user.getRole(), user.getProfileImage());

            String jwt = jwtService.generateToken(user.getEmail());
            log.info("[OAuth2-Callback] JWT token successfully generated for {}", user.getEmail());

            Cookie cookie = new Cookie("AUTH_TOKEN", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/api");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);

            String separator = frontendSuccessUrl.contains("?") ? "&" : "?";
            String targetUrl = frontendSuccessUrl + separator + "token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);
            log.info("[OAuth2-Callback] Redirecting authenticated user to frontend: {}", targetUrl);
            response.sendRedirect(targetUrl);
        } catch (Exception ex) {
            log.error("[OAuth2-Callback] Error processing OAuth2 authentication callback", ex);
            String failureRedirect = frontendSuccessUrl.replace("/oauth/callback", "/login");
            String separator = failureRedirect.contains("?") ? "&" : "?";
            response.sendRedirect(failureRedirect + separator + "error=" + URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private AuthProvider providerFor(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "linkedin" -> AuthProvider.LINKEDIN;
            default -> throw new IllegalArgumentException("Unsupported OAuth provider: " + registrationId);
        };
    }

    private String extractEmail(Map<String, Object> attributes) {
        Object value = attributes.get("email");
        if (value == null || value.toString().isBlank()) {
            value = attributes.get("email_address");
        }
        if (value == null || value.toString().isBlank()) {
            value = attributes.get("preferred_username");
        }
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("OAuth provider did not return an email address. Ensure email scope is granted.");
        }
        return value.toString().trim().toLowerCase();
    }
}