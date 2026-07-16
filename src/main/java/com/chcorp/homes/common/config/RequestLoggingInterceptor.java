package com.chcorp.homes.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = RequestLoggingInterceptor.class.getName() + ".startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.nanoTime());

        String query = request.getQueryString();
        if (query == null || query.isBlank()) {
            log.info("REQUEST method={} uri={}", request.getMethod(), request.getRequestURI());
        } else {
            log.info("REQUEST method={} uri={} query={}", request.getMethod(), request.getRequestURI(), query);
        }

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        Object startTime = request.getAttribute(START_TIME_ATTRIBUTE);
        long elapsedMs = 0L;
        if (startTime instanceof Long startTimeNanos) {
            elapsedMs = (System.nanoTime() - startTimeNanos) / 1_000_000;
        }

        String query = request.getQueryString();
        if (query == null || query.isBlank()) {
            log.info(
                    "RESPONSE method={} uri={} status={} elapsedMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    elapsedMs
            );
        } else {
            log.info(
                    "RESPONSE method={} uri={} query={} status={} elapsedMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    query,
                    response.getStatus(),
                    elapsedMs
            );
        }
    }
}
