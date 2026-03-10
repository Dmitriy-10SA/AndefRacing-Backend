package ru.andef.andefracing.backend.network;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerRestControllerAdvice {
    private static final String VALIDATION_ERROR = "Validation error";

    /**
     * Создаёт стандартный ответ об ошибке
     */
    private ResponseEntity<ErrorDto> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request
    ) {
        ErrorDto errorDto = new ErrorDto(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(errorDto);
    }

    /**
     * Обработка ошибок валидации тела запроса (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_ERROR, message, request);
    }

    /**
     * Обработка ошибок валидации параметров запроса (@RequestParam и т.д.)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_ERROR, message, request);
    }

    /**
     * DTO стандартного ответа об ошибке
     */
    public record ErrorDto(Instant timestamp, int status, String error, String message, String path) {
    }
}