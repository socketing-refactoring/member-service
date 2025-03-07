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
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String apiName = request.getRequestURI();
        String userId = request.getHeader("X-User-ID");

        filterChain.doFilter(requestWrapper, responseWrapper);

        byte[] requestBody = requestWrapper.getContentAsByteArray();
        log.info(
                "API: {}, User: {}, Request: {}",
                apiName,
                userId,
                new String(requestBody, StandardCharsets.UTF_8));

        byte[] responseBody = responseWrapper.getContentAsByteArray();
        log.info(
                "API: {}, User: {}, Response: {}",
                apiName,
                userId,
                new String(responseBody, StandardCharsets.UTF_8));

        responseWrapper.copyBodyToResponse();
    }
}
