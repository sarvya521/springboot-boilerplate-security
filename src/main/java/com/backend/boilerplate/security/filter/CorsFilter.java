package com.backend.boilerplate.security.filter;

import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
public class CorsFilter extends OncePerRequestFilter {

    public static final String CORS_HEADERS = "Accept,Accept-Language,Content-Language,Content-Type,Authorization";
    private List<String> allowedOrigins;

    public CorsFilter(String allowedUrls) {
        allowedOrigins = Optional.ofNullable(allowedUrls)
                .map(urls -> Arrays.stream(urls.split(",")).collect(Collectors.toList())).orElse(
                        Collections.emptyList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            //addXTraceId(request);
            String origin = request.getHeader("origin");
            boolean hasMatch = hasAllowedOrigin(origin);
            if (hasMatch) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            } else {
                log.debug("origin {} not matched with allowed list", origin);
            }
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Headers",CORS_HEADERS);
            //response.setHeader(X_TRACE_ID, MDC.get(X_TRACE_ID));
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private boolean hasAllowedOrigin(String origin) {
        boolean hasMatch = Optional.ofNullable(origin)
            .map(headerValue -> allowedOrigins.stream().anyMatch(s -> s.equals(headerValue)))
            .orElse(false);
        if (!hasMatch) {
            hasMatch = Optional.ofNullable(origin)
                .map(headerValue -> allowedOrigins.stream().map(s -> this.createMatcher(origin,s))
                    .anyMatch(matcher -> matcher.matches()))
                .orElse(false);
        }
        return hasMatch;
    }

    private Matcher createMatcher(String origin, String allowedOrigin) {
        String regex = this.parseAllowedWildcardOriginToRegex(allowedOrigin);
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(origin);
    }

    private String parseAllowedWildcardOriginToRegex(String allowedOrigin) {
        String regex = allowedOrigin.replace(".", "\\.");
        return regex.replace("*", ".*");
    }

    /*private void addXTraceId(HttpServletRequest request) {
        String xTraceId = request.getHeader(X_TRACE_ID);
        log.info("{} value # {}" , X_TRACE_ID, xTraceId);
        if (xTraceId == null) {
            MDC.put(X_TRACE_ID, UUID.randomUUID().toString());
        } else {
            MDC.put(X_TRACE_ID, xTraceId);
        }
    }*/
}
