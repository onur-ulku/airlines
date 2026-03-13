package com.example.airlines.config;

import com.example.airlines.exception.ApiErrorResponse;
import com.example.airlines.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        if (ex instanceof CustomException custom) {
            return ResponseEntity
                    .status(custom.getStatus())
                    .body(new ApiErrorResponse(custom.getCode(), custom.getMessage()));
        }

        String action = describeAction(request);
        String message = "Something went wrong while " + action + ". Please try again later.";

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("INTERNAL_ERROR", message));
    }

    private static String describeAction(HttpServletRequest request) {
        String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method)) {
            return "creating this record";
        }
        if ("PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            return "updating this record";
        }
        if ("DELETE".equalsIgnoreCase(method)) {
            return "deleting this record";
        }
        if ("GET".equalsIgnoreCase(method)) {
            return "processing this request";
        }
        return "performing this operation";
    }
}

