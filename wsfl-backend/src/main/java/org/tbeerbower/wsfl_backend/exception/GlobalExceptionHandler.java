package org.tbeerbower.wsfl_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.tbeerbower.wsfl_backend.api.WsflResponse;

import jakarta.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<WsflResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        WsflResponse<Void> response = new WsflResponse<>(null, List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<WsflResponse<Void>> handleValidation(ValidationException ex) {
        WsflResponse<Void> response = new WsflResponse<>(null, List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WsflResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        WsflResponse<Void> response = new WsflResponse<>(null, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<WsflResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        WsflResponse<Void> response = new WsflResponse<>(null, List.of("Access denied"));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<WsflResponse<Void>> handleNoHandlerFound(NoHandlerFoundException ex) {
        String message = String.format("Could not find the %s method for URL %s", 
            ex.getHttpMethod(), ex.getRequestURL());
        WsflResponse<Void> response = new WsflResponse<>(null, List.of(message));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WsflResponse<Void>> handleGeneral(Exception ex) {
        WsflResponse<Void> response = new WsflResponse<>(null, List.of("An unexpected error occurred"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
