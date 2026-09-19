package com.lms.Backend.auth;

import com.lms.Backend.auth.security.JwtService;
import com.lms.Backend.auth.security.OAuth2SuccessHandler;
import com.lms.Backend.user.entity.AuthProvider;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OAuth2ProfileImagePersistenceTest {

    private UserRepository userRepository;
    private JwtService jwtService;
    private OAuth2SuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtService = mock(JwtService.class);
        when(jwtService.generateToken(any())).thenReturn("mock-jwt-token");
        successHandler = new OAuth2SuccessHandler(userRepository, jwtService, "http://localhost:3000/oauth/callback");
    }

    @Test
    @DisplayName("Existing user with uploaded profile image must NOT have profile_image overwritten by Google OAuth picture")
    void existingUser_profileImageMustNotBeOverwritten() throws Exception {
        String testEmail = "learner@example.com";
        String existingUploadedPath = "/uploads/profile-images/user-123.jpg";
        String googlePictureUrl = "https://lh3.googleusercontent.com/a/ACg8ocK12345";

        User existingUser = new User();
        existingUser.setName("Original Name");
        existingUser.setEmail(testEmail);
        existingUser.setRole(UserRole.STUDENT);
        existingUser.setProfileImage(existingUploadedPath);

        when(userRepository.findByProviderAndProviderId(any(), any())).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase(testEmail)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);
        when(authToken.getAuthorizedClientRegistrationId()).thenReturn("google");

        OAuth2User oauthUser = mock(OAuth2User.class);
        when(oauthUser.getName()).thenReturn("google-sub-999");
        when(oauthUser.getAttributes()).thenReturn(Map.of(
            "sub", "google-sub-999",
            "email", testEmail,
            "name", "Google Display Name",
            "picture", googlePictureUrl
        ));
        when(authToken.getPrincipal()).thenReturn(oauthUser);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        successHandler.onAuthenticationSuccess(request, response, authToken);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        // CRITICAL CHECK: existing profile_image must remain unchanged!
        assertEquals(existingUploadedPath, savedUser.getProfileImage(),
            "Existing user profile_image must NEVER be overwritten with Google profile picture!");
        assertEquals(testEmail, savedUser.getEmail());
        assertEquals(AuthProvider.GOOGLE, savedUser.getProvider());
    }

    @Test
    @DisplayName("Completely new user signing in with Google OAuth receives Google profile picture")
    void newUser_receivesGoogleProfilePicture() throws Exception {
        String newEmail = "newuser@example.com";
        String googlePictureUrl = "https://lh3.googleusercontent.com/a/ACg8ocK-new-user";

        when(userRepository.findByProviderAndProviderId(any(), any())).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase(newEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);
        when(authToken.getAuthorizedClientRegistrationId()).thenReturn("google");

        OAuth2User oauthUser = mock(OAuth2User.class);
        when(oauthUser.getName()).thenReturn("google-sub-111");
        when(oauthUser.getAttributes()).thenReturn(Map.of(
            "sub", "google-sub-111",
            "email", newEmail,
            "name", "Brand New User",
            "picture", googlePictureUrl
        ));
        when(authToken.getPrincipal()).thenReturn(oauthUser);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        successHandler.onAuthenticationSuccess(request, response, authToken);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(googlePictureUrl, savedUser.getProfileImage(),
            "Brand new user should receive provider default image");
        assertEquals(newEmail, savedUser.getEmail());
        assertEquals(AuthProvider.GOOGLE, savedUser.getProvider());
    }
}
