package com.app.join.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice fängt Exceptions aus ALLEN Controllern zentral ab.
// Kein try/catch mehr im Controller nötig — der wirft einfach und dieser Handler kümmert sich drum.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Wird aufgerufen wenn irgendwo eine IllegalArgumentException geworfen wird (z.B. "User already exists")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    // Wird aufgerufen bei falschen Login-Daten
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(SecurityException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage()));
    }

    // Fallback für alle anderen unerwarteten Fehler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred"));
    }

    public record ErrorResponse(String message) {}
}
