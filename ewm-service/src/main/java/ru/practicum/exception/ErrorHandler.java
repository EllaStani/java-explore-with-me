package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Validated
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.error("400 {}", e.getMessage());
        return Map.of("status", "400 - BAD_REQUEST",
                "reason", "Incorrectly made request.",
                "errorMessage", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.error("404 {}", e.getMessage());
        return Map.of("status", "404 - NOT_FOUND",
                "reason", "The required object was not found.",
                "errorMessage", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRequestException(final RequestException e) {
        log.error("400 {}", e.getMessage());
        return Map.of("status", "400 - BAD_REQUEST",
                "reason", "Incorrectly made request.",
                "errorMessage", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(final ConflictException e) {
        log.error("Исключение ConflictException");
        return Map.of("status", "409 - CONFLICT",
                "reason", "Integrity constraint has been violated.",
                "errorMessage", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    private ResponseEntity<Object> handlePSQLException(DataIntegrityViolationException e) {
        log.info("409 {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("status", "409 - CONFLICT",
                        "reason", "Нарушение целостности данных.",
                        "timestamp", LocalDateTime.now().toString()), HttpStatus.CONFLICT);

    }
}
