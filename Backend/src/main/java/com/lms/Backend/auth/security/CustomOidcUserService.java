package com.lms.Backend.auth.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOidcUserService.class);

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("[OIDC] Initiating userinfo retrieval for provider: {}", registrationId);
        try {
            OidcUser oidcUser = super.loadUser(userRequest);
            log.info("[OIDC] User claims successfully retrieved for {}: sub={}, email={}",
                registrationId,
                oidcUser.getSubject(),
                oidcUser.getEmail() != null ? oidcUser.getEmail() : oidcUser.getClaims().get("email"));
            return oidcUser;
        } catch (OAuth2AuthenticationException ex) {
            log.error("[OIDC] Failed to retrieve user from {}: {}", registrationId, ex.getMessage(), ex);
            throw ex;
        }
    }
}
