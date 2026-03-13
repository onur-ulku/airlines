package com.example.airlines.config;

import com.example.airlines.exception.ApiErrorResponse;
import com.example.airlines.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleException_withCustomException_returnsCodeAndMessage() {
        CustomException ex = new CustomException("LOCATION_IN_USE", "Location in use", HttpStatus.CONFLICT);

        ResponseEntity<ApiErrorResponse> res = handler.handleException(ex, null);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().code()).isEqualTo("LOCATION_IN_USE");
        assertThat(res.getBody().message()).isEqualTo("Location in use");
    }

    @Test
    void handleException_withGenericException_returnsInternalError() {
        Exception ex = new RuntimeException("boom");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");

        ResponseEntity<ApiErrorResponse> res = handler.handleException(ex, request);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(res.getBody().message()).isEqualTo("Something went wrong while processing this request. Please try again later.");
    }
}
