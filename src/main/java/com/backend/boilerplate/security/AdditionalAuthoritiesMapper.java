package com.backend.boilerplate.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public interface AdditionalAuthoritiesMapper {
    List<GrantedAuthority> mapAuthorities(String principal, List<GrantedAuthority> grantedAuthorities);
}
