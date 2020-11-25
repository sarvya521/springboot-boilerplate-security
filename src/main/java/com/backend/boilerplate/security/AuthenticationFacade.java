package com.backend.boilerplate.security;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AuthenticationFacade implements IAuthenticationFacade {

    private final String USER_EMAIL_KEY = "userEmail";

    private final String USER_FULL_NAME_KEY = "userFullName";

    public UUID getUserId() {
        Authentication authentication = getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AuthenticationServiceException("No Authentication object found in SecurityContext");
        }
        return authentication;
    }

    public List<GrantedAuthority> getUserRoles() {
        Authentication authentication = getAuthentication();
        return new ArrayList<>(authentication.getAuthorities());
    }

    public String getUserFullName() {
        Object userFullName = RequestContextHolder.getRequestAttributes()
            .getAttribute(USER_FULL_NAME_KEY, RequestAttributes.SCOPE_REQUEST);
        return userFullName != null ? (String) userFullName : null;
    }

    public String getUserEmail() {
        Object userEmail = RequestContextHolder.getRequestAttributes()
            .getAttribute(USER_EMAIL_KEY, RequestAttributes.SCOPE_REQUEST);
        return userEmail != null ? (String) userEmail : null;
    }
}
