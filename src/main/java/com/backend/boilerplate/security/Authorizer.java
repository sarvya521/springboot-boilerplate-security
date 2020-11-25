package com.backend.boilerplate.security;

import com.backend.boilerplate.exception.ResourceNotFoundException;
import com.backend.boilerplate.security.util.HandlerMappingUriReader;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Objects;

import static com.backend.boilerplate.constant.AuthorityPrefix.CLAIM_;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
public final class Authorizer {

    private Authorizer() {
        throw new AssertionError();
    }

    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    public static void doAuthorize(final HttpServletRequest request,
                                   final com.backend.boilerplate.security.IAuthenticationFacade authenticationFacade,
                                   final HandlerMapping handlerMapping) throws AccessDeniedException {
        String resourceHttpMethod = request.getMethod();
        log.debug("User {} is trying to access {}-{}",
                authenticationFacade.getAuthentication().getPrincipal(),
                resourceHttpMethod,
                request.getRequestURI());
        HandlerExecutionChain handlerExecutionChain = checkHandlerMapping(request, handlerMapping);
        String resourceEndpoint = parseApiUri(request, (HandlerMethod) handlerExecutionChain.getHandler());
        boolean isUserAuthorized = authenticationFacade.getUserRoles().stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith(String.valueOf(CLAIM_)))
                .map(grantedAuthority -> {
                    try {
                        return ClaimUtil.parseClaimDto(grantedAuthority);
                    } catch (IOException e) {
                        throw new AuthenticationServiceException("Claim could not be parsed", e);
                    }
                })
                .anyMatch(claimDto -> Objects.equals(claimDto.getResourceHttpMethod(), resourceHttpMethod)
                        && Objects.equals(claimDto.getResourceEndpoint(), resourceEndpoint));
        if (isUserAuthorized && log.isDebugEnabled()) {
            log.debug("User {} is allowed to access the resource {}-{}",
                    authenticationFacade.getAuthentication().getPrincipal(),
                    resourceHttpMethod,
                    resourceEndpoint);
        }
        if (!isUserAuthorized) {
            log.warn("User {} is not allowed to access the resource {}-{}",
                    authenticationFacade.getAuthentication().getPrincipal(),
                    resourceHttpMethod,
                    resourceEndpoint);
            throw new AccessDeniedException("Forbidden");
        }
    }

    static boolean isApiUriMatched(@NotNull String resourceEndpoint, @NotNull String apiUri) {
        if (resourceEndpoint.equals(apiUri)) {
            return true;
        }
        String[] resourceEndpointPaths = resourceEndpoint.split("/");
        String[] apiUriPaths = apiUri.split("/");

        if (resourceEndpointPaths.length != apiUriPaths.length) {
            return false;
        }

        for (int i = 0; i < resourceEndpointPaths.length; i++) {
            if (apiUriPaths[i].startsWith("{") && apiUriPaths[i].endsWith("}")) {
                continue;
            }
            if (!Objects.equals(resourceEndpointPaths[i], apiUriPaths[i])) {
                return false;
            }
        }
        return true;
    }

    private static HandlerExecutionChain checkHandlerMapping(final HttpServletRequest request,
                                                             final HandlerMapping handlerMapping) {
        HandlerExecutionChain handlerExecutionChain;
        try {
            handlerExecutionChain = handlerMapping.getHandler(request);
            if (Objects.isNull(handlerExecutionChain)) {
                throw new ResourceNotFoundException("404-RequestMapping not found for current request");
            }
        } catch (Exception e) {
            log.error("404-RequestMapping not found for current request", e);
            throw new ResourceNotFoundException("404-RequestMapping not found for current request");
        }
        return handlerExecutionChain;
    }

    private static String parseApiUri(final HttpServletRequest request, final HandlerMethod handlerMethod) {
        String resourceEndpoint = request.getRequestURI();
        String[] apiUris = HandlerMappingUriReader.readUris(handlerMethod,
                HttpMethod.valueOf(request.getMethod()));
        for (String apiUri : apiUris) {
            if (isApiUriMatched(resourceEndpoint, apiUri)) {
                return apiUri;
            }
        }
        return resourceEndpoint;
    }
}
