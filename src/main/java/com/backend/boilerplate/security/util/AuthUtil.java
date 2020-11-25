package com.backend.boilerplate.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.boilerplate.dto.UserDetailsDto;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static com.backend.boilerplate.constant.AuthorityPrefix.CLAIM_;
import static com.backend.boilerplate.constant.AuthorityPrefix.FEATURE_CLAIM_;
import static com.backend.boilerplate.constant.AuthorityPrefix.ROLE_;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class AuthUtil {

    public static List<GrantedAuthority> getAuthorities(@NotNull UserDetailsDto userDetailsDto) {
        List<GrantedAuthority> authorities =
                userDetailsDto.getRoles().stream()
                        .map(roleDto ->
                                new SimpleGrantedAuthority(ROLE_ + roleDto.getName()))
                        .collect(Collectors.toList());

        final ObjectMapper objectMapper = new ObjectMapper();

        userDetailsDto.getRoles().stream()
                .flatMap(roleDto -> roleDto.getClaims().stream())
                .forEach(claimDto -> {
                    GrantedAuthority grantedAuthority = null;
                    try {
                        grantedAuthority =
                                new SimpleGrantedAuthority(
                                        CLAIM_ + objectMapper.writeValueAsString(claimDto)
                                );
                        authorities.add(grantedAuthority);
                    } catch (JsonProcessingException e) {
                        throw new AuthenticationServiceException("Claim could not be parsed", e);
                    }
                });

        userDetailsDto.getRoles().stream()
                .flatMap(roleDto -> roleDto.getFeatures().stream())
                .forEach(featureDto -> {
                    GrantedAuthority grantedAuthority = null;
                    try {
                        grantedAuthority =
                                new SimpleGrantedAuthority(
                                        FEATURE_CLAIM_ + objectMapper.writeValueAsString(featureDto)
                                );
                        authorities.add(grantedAuthority);
                    } catch (JsonProcessingException e) {
                        throw new AuthenticationServiceException("Feature claim could not be parsed", e);
                    }
                });
        return authorities;
    }

    private AuthUtil() {
        throw new AssertionError();
    }
}
