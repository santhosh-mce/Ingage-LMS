package com.lms.Backend.auth.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("[OAuth2] Loading userinfo from provider: {}", registrationId);
        try {
            OAuth2User user = super.loadUser(userRequest);
            log.info("[OAuth2] Attributes retrieved for {}: {}", registrationId, user.getAttributes().keySet());
            return user;
        } catch (OAuth2AuthenticationException ex) {
            log.error("[OAuth2] Failed to load user from {}: {}", registrationId, ex.getMessage(), ex);
            throw ex;
        }
    }
}