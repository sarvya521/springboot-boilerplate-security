package com.backend.boilerplate.security;

import com.backend.boilerplate.constant.Status;
import com.backend.boilerplate.dto.Response;
import com.backend.boilerplate.exception.ErrorDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
public class SpAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    private static final String ERROR_MESSAGE = "You are not allowed to access this resource";

    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("user is not allowed to access the resource");
        SecurityContextHolder.clearContext();

        try (ServletServerHttpResponse res = new ServletServerHttpResponse(response)) {
            res.setStatusCode(HttpStatus.FORBIDDEN);
            res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            Response owResponse = new Response();
            owResponse.setStatus(Status.FAIL);
            owResponse.setCode(HttpStatus.FORBIDDEN.value());
            ErrorDetails errorDetails = new ErrorDetails(String.valueOf(HttpStatus.FORBIDDEN.value()), ERROR_MESSAGE);
            List<ErrorDetails> errors = new ArrayList<>();
            errors.add(errorDetails);
            owResponse.setErrors(errors);

            res.getBody().write(new ObjectMapper().writeValueAsString(owResponse).getBytes());
        }
    }
}
