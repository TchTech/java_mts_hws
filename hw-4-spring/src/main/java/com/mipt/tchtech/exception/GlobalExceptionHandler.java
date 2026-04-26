package com.mipt.tchtech.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Bad credentials for request {}", request.getRequestURI());
        return problem(401, "Unauthorized", "Неверные учётные данные", request.getRequestURI());
    }


    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFound(
            TaskNotFoundException ex, HttpServletRequest request) {
        log.info("Task not found: {}", ex.getMessage());
        return problem(404, "Task Not Found", ex.getMessage(), request.getRequestURI());
    }


    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(
            RequestNotPermitted ex, HttpServletRequest request) {
        log.warn("Rate limit exceeded for {}: {}", request.getRequestURI(), ex.getMessage());
        Map<String, Object> body = buildProblem(429, "Too Many Requests",
                "Превышен лимит запросов к внешнему API — попробуйте позже", request.getRequestURI());
        return ResponseEntity.status(429)
                .header("Retry-After", "10")
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }


    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> handleCircuitOpen(
            CallNotPermittedException ex, HttpServletRequest request) {
        log.warn("Circuit breaker OPEN for {}: {}", request.getRequestURI(), ex.getMessage());
        return problem(503, "Service Unavailable",
                "Внешний сервис временно недоступен (circuit breaker OPEN)", request.getRequestURI());
    }


    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Map<String, Object>> handleExternalApi(
            ExternalApiException ex, HttpServletRequest request) {
        log.error("External API error for {}: {}", request.getRequestURI(), ex.getMessage());
        return problem(502, "Bad Gateway", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return problem(500, "Internal Server Error", "Внутренняя ошибка сервера", request.getRequestURI());
    }


    private ResponseEntity<Map<String, Object>> problem(int status, String title,
                                                        String detail, String instance) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(buildProblem(status, title, detail, instance));
    }

    private Map<String, Object> buildProblem(int status, String title, String detail, String instance) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "about:blank");
        body.put("title", title);
        body.put("status", status);
        body.put("detail", detail);
        body.put("instance", instance);
        body.put("timestamp", Instant.now().toString());
        return body;
    }
}