package com.backend.boilerplate.security;

import com.backend.boilerplate.dto.Response;
import com.backend.boilerplate.dto.UserDetailsDto;
import com.backend.boilerplate.security.util.AuthUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpMethod.GET;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
public class DefaultAdditionalAuthoritiesMapper implements AdditionalAuthoritiesMapper {

    private final String userAuthApi;

    public DefaultAdditionalAuthoritiesMapper(String userAuthApi) {
        log.debug("configuring DefaultAdditionalAuthoritiesMapper");
        this.userAuthApi = userAuthApi;
    }

    @Override
    public List<GrantedAuthority> mapAuthorities(String principal, List<GrantedAuthority> grantedAuthorities) {
        RestTemplate restTemplate = new RestTemplate();
        final String api = userAuthApi.replaceFirst("\\{uuid\\}", principal);
        UserDetailsDto userDetailsDto = null;
        try {
            Response<UserDetailsDto> response =
                    restTemplate.exchange(api, GET, null,
                            new ParameterizedTypeReference<Response<UserDetailsDto>>() {
                            }).getBody();
            Objects.requireNonNull(response);
            userDetailsDto = response.getData();
        } catch (RestClientException rcex) {
            log.error("RestClientException for api {} - {} ", "userAuthApi", api);
        }
        Objects.requireNonNull(userDetailsDto);
        List<GrantedAuthority> authorities = AuthUtil.getAuthorities(userDetailsDto);
        log.debug("Authorities for user {} are: {}", () -> principal, () -> authorities);
        return authorities;
    }
}
