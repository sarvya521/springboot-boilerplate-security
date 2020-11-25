package com.backend.boilerplate.security;

import com.backend.boilerplate.exception.ErrorDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.boilerplate.constant.Status;
import com.backend.boilerplate.dto.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

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
public class SpAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    public static final String ERROR_MESSAGE = "Authorization token not present or invalid";

    public void commence(HttpServletRequest request, HttpServletResponse httpServletResponse,
                         AuthenticationException authException) throws
            IOException {
        log.warn("User is not authenticated");
        if (httpServletResponse.getStatus() == HttpStatus.FORBIDDEN.value()) {
            return;
        }

        try (ServletServerHttpResponse res = new ServletServerHttpResponse(httpServletResponse)) {
            res.setStatusCode(HttpStatus.UNAUTHORIZED);
            res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            Response response = new Response();
            response.setStatus(Status.FAIL);
            response.setCode(HttpStatus.UNAUTHORIZED.value());
            ErrorDetails error = new ErrorDetails(String.valueOf(HttpStatus.UNAUTHORIZED.value()), ERROR_MESSAGE);
            List<ErrorDetails> errors = new ArrayList<>();
            errors.add(error);
            response.setErrors(errors);

            res.getBody().write(new ObjectMapper().writeValueAsString(response).getBytes());
        }
    }
}
