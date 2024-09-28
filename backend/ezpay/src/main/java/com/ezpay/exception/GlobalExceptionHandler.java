package com.ezpay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**Author: Sandarbha Komujwar
 * Date:08/09/2024
 * Module:Password Recovery 
 */
/**
 * Global exception handler class to manage exceptions throughout the application.
 * It provides specific and generic exception handling methods and returns custom error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
     * Handles RuntimeException and returns a custom error response with a BAD_REQUEST status.
     *
     * @param ex the RuntimeException that occurred
     * @param request the current web request
     * @return a ResponseEntity with the error details and BAD_REQUEST status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
    	// Business Logic: Handle specific runtime exceptions and return error details with timestamp and message
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all generic exceptions and returns a custom error response with an INTERNAL_SERVER_ERROR status.
     *
     * @param ex the generic exception that occurred
     * @param request the current web request
     * @return a ResponseEntity with the error details and INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
    	  // Business Logic: Handle generic exceptions and return a custom error message
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "An error occurred");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
