package com.backend.boilerplate.security;

import com.backend.boilerplate.dto.ClaimDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;

import static com.backend.boilerplate.constant.AuthorityPrefix.CLAIM_;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class ClaimUtil {
    private ClaimUtil() {
        throw new AssertionError();
    }

    public static ClaimDto parseClaimDto(final GrantedAuthority grantedAuthority) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        return
                objectMapper
                        .readValue(
                                grantedAuthority
                                        .getAuthority()
                                        .replace(String.valueOf(CLAIM_), "")
                                , ClaimDto.class
                        );
    }
}
