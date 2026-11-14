
package com.hotelbooking.exception;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> createErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        Map<String, Object> error = new LinkedHashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        error.put("path", request.getRequestURI());

        return error;
    }

    // Handle Runtime Exceptions
  
@ExceptionHandler(RuntimeException.class)
public ResponseEntity<Map<String, Object>> handleRuntimeException(
        RuntimeException ex,
        HttpServletRequest request) {

    HttpStatus status;

    if ("Room is already booked for the selected dates"
            .equals(ex.getMessage())) {
        status = HttpStatus.CONFLICT;
    } else {
        status = HttpStatus.BAD_REQUEST;
    }

    return ResponseEntity
            .status(status)
            .body(createErrorResponse(
                    status,
                    ex.getMessage(),
                    request));
}

    // Handle Invalid JSON Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Invalid request body or date format",
                        request));
    }

    // Handle Other Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Something went wrong. Please try again later.",
                        request));
    }
    

@ExceptionHandler(ResponseStatusException.class)
public ResponseEntity<Map<String, Object>> handleResponseStatusException(
        ResponseStatusException ex,
        HttpServletRequest request) {

    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

    return ResponseEntity
            .status(status)
            .body(createErrorResponse(
                    status,
                    ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(),
                    request));
}

}
