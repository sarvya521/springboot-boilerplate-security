package com.backend.boilerplate.security.filter;

import com.backend.boilerplate.security.Authorizer;
import com.backend.boilerplate.security.IAuthenticationFacade;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.backend.boilerplate.security.SpAuthenticationEntryPoint.ERROR_MESSAGE;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
public class AuthorizationFilter extends OncePerRequestFilter {

    private final IAuthenticationFacade authenticationFacade;

    private final AccessDeniedHandler accessDeniedHandler;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final HandlerMapping handlerMapping;

    public AuthorizationFilter(final IAuthenticationFacade authenticationFacade,
                               final AccessDeniedHandler accessDeniedHandler,
                               final AuthenticationEntryPoint authenticationEntryPoint,
                               final HandlerMapping handlerMapping) {
        this.authenticationFacade = authenticationFacade;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filters)
            throws ServletException, IOException {
        if (Objects.isNull(authenticationFacade.getAuthentication())) {
            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException(ERROR_MESSAGE));
            return;
        }
        log.debug("authorizing user access");
        try {
            Authorizer.doAuthorize(request, authenticationFacade, handlerMapping);
        } catch (AccessDeniedException e) {
            accessDeniedHandler.handle(request, response, e);
            return;
        }
        filters.doFilter(request, response);
    }
}
