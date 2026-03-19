package ru.andef.andefracing.backend.network;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andef.andefracing.backend.domain.exceptions.*;
import ru.andef.andefracing.backend.domain.exceptions.auth.ClientWithThisPhoneAlreadyExistsException;
import ru.andef.andefracing.backend.domain.exceptions.auth.InvalidPhoneOrPasswordException;
import ru.andef.andefracing.backend.domain.exceptions.booking.InvalidBookingSlotException;
import ru.andef.andefracing.backend.domain.exceptions.booking.NotEnoughSimulatorsException;
import ru.andef.andefracing.backend.domain.exceptions.management.*;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final String VALIDATION_ERROR = "Validation error";
    private static final String AUTH_ERROR = "Auth error";
    private static final String ENTITY_NOT_FOUND_ERROR = "Entity not found";
    private static final String DUPLICATE_ERROR = "Duplicate error";
    private static final String CONDITIONS_NOT_MET_ERROR = "Conditions not met error";
    private static final String BLOCKED_ERROR = "Blocked error";
    private static final String ILLEGAL_ARGUMENT_ERROR = "Illegal argument error";

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
     * Получение HttpStatus из исключения
     */
    private HttpStatus getHttpStatus(RuntimeException ex) {
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        return (responseStatus != null) ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Обработка ошибок, связанных с дублированием
     */
    @ExceptionHandler(value = DuplicateException.class)
    public ResponseEntity<ErrorDto> handleDuplicateExceptions(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(getHttpStatus(ex), DUPLICATE_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибок, связанных с неправильными аргументами
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ILLEGAL_ARGUMENT_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибок блокировки
     */
    @ExceptionHandler(value = BlockedException.class)
    public ResponseEntity<ErrorDto> handleBlockedExceptions(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(getHttpStatus(ex), BLOCKED_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибок, связанных с несоответствием условиям
     */
    @ExceptionHandler(
            value = {
                    ClubOpenConditionsNotMetException.class,
                    ClubCloseConditionsNotMetException.class,
                    InvalidWorkScheduleException.class,
                    InvalidBookingSlotException.class,
                    NotEnoughSimulatorsException.class,
                    CannotAddExceptionDayDueToExistingBookingsException.class,
                    InvalidDateRangeException.class
            }
    )
    public ResponseEntity<ErrorDto> handleConditionsNotMetExceptions(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(getHttpStatus(ex), CONDITIONS_NOT_MET_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка Auth ошибок
     */
    @ExceptionHandler(
            value = {
                    InvalidPhoneOrPasswordException.class,
                    ClientWithThisPhoneAlreadyExistsException.class,
                    EmployeeWithThisPhoneAlreadyExistsException.class,
                    PasswordIsNotSetException.class
            }
    )
    public ResponseEntity<ErrorDto> handleAuthExceptions(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(getHttpStatus(ex), AUTH_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки сущность не найдена
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(getHttpStatus(ex), ENTITY_NOT_FOUND_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибок валидации тела запроса (@Valid)
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
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
    @ExceptionHandler(value = ConstraintViolationException.class)
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