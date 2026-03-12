package com.example.airlines.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResponseStatus_returnsStatusAndMessage() {
        var ex = new ResponseStatusException(HttpStatus.CONFLICT, "Location in use");
        ResponseEntity<Map<String, String>> res = handler.handleResponseStatus(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(res.getBody()).containsEntry("message", "Location in use");
    }

    @Test
    void handleResponseStatus_nullReason_returnsDefaultMessage() {
        var ex = new ResponseStatusException(HttpStatus.BAD_REQUEST);
        ResponseEntity<Map<String, String>> res = handler.handleResponseStatus(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).containsEntry("message", "Operation failed.");
    }
}
