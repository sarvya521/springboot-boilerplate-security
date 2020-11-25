package com.backend.boilerplate.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

public interface IAuthenticationFacade {
    List<GrantedAuthority> getUserRoles();

    Authentication getAuthentication();

    UUID getUserId();

    String getUserFullName();

    String getUserEmail();
}
