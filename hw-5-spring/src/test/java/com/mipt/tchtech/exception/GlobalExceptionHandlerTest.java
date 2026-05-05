package com.mipt.tchtech.exception;

import com.mipt.tchtech.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private HttpServletRequest request;

    @Test
    void taskNotFound_returns404WithMessage() {
        given(request.getRequestURI()).willReturn("/api/tasks/42");

        ResponseEntity<ErrorResponse> response =
                handler.handleTaskNotFound(new TaskNotFoundException(42L), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).contains("42");
        assertThat(response.getBody().getPath()).isEqualTo("/api/tasks/42");
    }

    @Test
    void notReadable_returns400() {
        given(request.getRequestURI()).willReturn("/api/tasks");

        ResponseEntity<ErrorResponse> response = handler.handleNotReadable(
                new HttpMessageNotReadableException("bad",
                        new org.springframework.http.HttpInputMessage() {
                            @Override
                            public java.io.InputStream getBody() {
                                return java.io.InputStream.nullInputStream();
                            }

                            @Override
                            public org.springframework.http.HttpHeaders getHeaders() {
                                return new org.springframework.http.HttpHeaders();
                            }
                        }),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Некорректный JSON");
    }

    @Test
    void bulkOperation_returns400WithDetails() {
        given(request.getRequestURI()).willReturn("/api/tasks/bulk-complete");

        ResponseEntity<ErrorResponse> response = handler.handleBulkOperation(
                new BulkOperationException(java.util.List.of(1L, 2L)), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("1", "2");
    }

    @Test
    void generic_returns500() {
        given(request.getRequestURI()).willReturn("/api/tasks");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(
                new IllegalStateException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
    }
}
