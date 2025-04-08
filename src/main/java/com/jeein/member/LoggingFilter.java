package com.jeein.member;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final String SWAGGER_PATH = "/api/v1/members/api/";
    private static final String UPLOAD_PATH = "/upload/";
    private static final long MAX_LOG_SIZE = 1024 * 10;
    private static final String ACTUATOR_PATH = "/actuator";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isSwaggerRequest(request) || isActuatorRequest(request) || isImageRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        byte[] requestBody = requestWrapper.getContentAsByteArray();
        if (requestBody.length <= MAX_LOG_SIZE) {
            log.info(
                    "request : {uri: {}, method: {}, body: {}}",
                    request.getRequestURI(),
                    request.getMethod(),
                    new String(requestBody, StandardCharsets.UTF_8));
        } else {
            log.info(
                    "request : {uri: {}, method: {}, body: request body is too large to log}",
                    request.getRequestURI(),
                    request.getMethod());
        }

        byte[] responseBody = responseWrapper.getContentAsByteArray();
        if (responseBody.length <= MAX_LOG_SIZE) {
            log.info(
                    "response : {status: {}, body: {}}",
                    response.getStatus(),
                    new String(responseBody, StandardCharsets.UTF_8));
        } else {
            log.info(
                    "response : {status: {}, body: response body is too large to log}",
                    response.getStatus());
        }

        responseWrapper.copyBodyToResponse();
    }

    private boolean isSwaggerRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.contains(SWAGGER_PATH);
    }

    private boolean isActuatorRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.contains(ACTUATOR_PATH);
    }

    private boolean isImageRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.contains(UPLOAD_PATH);
    }
}
