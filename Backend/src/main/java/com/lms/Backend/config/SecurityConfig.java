package com.lms.Backend.config;

import com.lms.Backend.auth.security.JwtAuthenticationFilter;
import com.lms.Backend.auth.security.CustomOAuth2UserService;
import com.lms.Backend.auth.security.CustomOidcUserService;
import com.lms.Backend.auth.security.OAuth2SuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;
    private final String frontendLoginUrl;

    public SecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        CustomOAuth2UserService customOAuth2UserService,
        CustomOidcUserService customOidcUserService,
        OAuth2SuccessHandler oauth2SuccessHandler,
        @Value("${app.frontend-urls}") String frontendUrls
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
        this.oauth2SuccessHandler = oauth2SuccessHandler;
        String baseFrontendUrl = Arrays.stream(frontendUrls.split(","))
            .map(String::trim)
            .findFirst()
            .orElse("http://localhost:3000");
        this.frontendLoginUrl = baseFrontendUrl + "/login";
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        ObjectProvider<ClientRegistrationRepository> oauthClientRegistrations
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/docs", "/api-docs", "/index.html", "/favicon.ico", "/error", "/css/**", "/js/**",
                    "/health", "/api/health",
                    "/auth/**", "/api/auth/**",
                    "/oauth2/**", "/api/oauth2/**",
                    "/login/**", "/api/login/**",
                    "/career/**", "/api/career/**",
                    "/careers/**", "/api/careers/**",
                    "/courses/**", "/api/courses/**",
                    "/certificates/verify/**", "/api/certificates/verify/**",
                    "/uploads/**", "/api/uploads/**",
                    "/discounts/validate", "/api/discounts/validate",
                    "/explore", "/api/explore", "/explore/**", "/api/explore/**",
                    "/projects", "/api/projects", "/projects/**", "/api/projects/**",
                    "/learning/**", "/api/learning/**",
                    "/webhooks/**", "/api/webhooks/**",
                    "/test/**", "/api/test/**"
                ).permitAll()
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (oauthClientRegistrations.getIfAvailable() != null) {
            http.oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                    .oidcUserService(customOidcUserService))
                .successHandler(oauth2SuccessHandler)
                .failureHandler((request, response, exception) -> {
                    log.error("[OAuth2-Failure] OAuth2 authentication failure: {}", exception.getMessage(), exception);
                    String redirectUrl = frontendLoginUrl + "?error=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(redirectUrl);
                }));
        }

        return http.build();
    }
}