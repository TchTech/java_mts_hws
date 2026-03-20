package com.mipt.tchtech.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.mipt.tchtech.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors().forEach(e ->
                fieldErrors.put(e.getObjectName(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(
                Instant.now(), 400, "Bad Request", "Ошибка валидации",
                request.getRequestURI(), fieldErrors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                Instant.now(), 400, "Bad Request", ex.getMessage(),
                request.getRequestURI(), null));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                Instant.now(), 400, "Bad Request", ex.getMessage(),
                request.getRequestURI(), null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                Instant.now(), 400, "Bad Request", "Некорректный JSON",
                request.getRequestURI(), null));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(
            NoHandlerFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                Instant.now(), 404, "Not Found", ex.getMessage(),
                request.getRequestURI(), null));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(
            TaskNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                Instant.now(), 404, "Not Found", ex.getMessage(),
                request.getRequestURI(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                Instant.now(), 500, "Internal Server Error", "Внутренняя ошибка сервера",
                request.getRequestURI(), null));
    }
}
