package com.example.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        HttpStatusCode status = exception.getStatusCode();
        String reason = exception.getReason();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.toString());
        body.put("message", reason == null || reason.isBlank() ? "Operation impossible" : reason);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        String message = exception.getMostSpecificCause() != null
                ? exception.getMostSpecificCause().getMessage()
                : exception.getMessage();

        if (message == null || message.isBlank()) {
            message = "Operation impossible a cause d une contrainte de base de donnees.";
        } else if (message.contains("FK_CHEF_AGENCE_NEW")) {
            message = "Chef agence invalide: MAT_CHEF_AGENCE doit correspondre au matricule d un utilisateur existant.";
        } else if (message.contains("ORA-02291")) {
            message = "Operation impossible: une cle etrangere reference une valeur inexistante.";
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 409);
        body.put("error", "409 CONFLICT");
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(409).body(body);
    }
}
