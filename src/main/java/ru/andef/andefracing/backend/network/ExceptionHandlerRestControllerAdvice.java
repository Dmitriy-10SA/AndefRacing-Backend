package ru.andef.andefracing.backend.network;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.auth.InvalidPhoneOrPasswordException;
import ru.andef.andefracing.backend.domain.exceptions.auth.client.ClientWithThisPhoneAlreadyExistsException;
import ru.andef.andefracing.backend.domain.exceptions.auth.client.ClientWithThisPhoneNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.auth.employee.EmployeeWithThisPhoneNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.management.*;
import ru.andef.andefracing.backend.domain.exceptions.profile.client.DuplicateFavoriteClubException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerRestControllerAdvice {
    private static final String VALIDATION_ERROR = "Validation error";
    private static final String AUTH_ERROR = "Auth error";
    private static final String ENTITY_NOT_FOUND_ERROR = "Entity not found";
    private static final String DUPLICATE_ERROR = "Duplicate error";
    private static final String CONDITIONS_NOT_MET_ERROR = "Сonditions not met error";

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
     * Обработка ошибки дубликат игры в клубе
     */
    @ExceptionHandler(DuplicateGameInClubException.class)
    public ResponseEntity<ErrorDto> handleDuplicateGameInClubException(
            DuplicateGameInClubException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, DUPLICATE_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки дубликат избранного клуба
     */
    @ExceptionHandler(DuplicateFavoriteClubException.class)
    public ResponseEntity<ErrorDto> handleDuplicateFavoriteClubException(
            DuplicateFavoriteClubException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, DUPLICATE_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки открытия клуба из-за несоответствия условиям открытия
     */
    @ExceptionHandler(ClubOpenConditionsNotMetException.class)
    public ResponseEntity<ErrorDto> handleClubOpenConditionsNotMetException(
            ClubOpenConditionsNotMetException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, CONDITIONS_NOT_MET_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки закрытия клуба из-за несоответствия условиям закрытия
     */
    @ExceptionHandler(ClubCloseConditionsNotMetException.class)
    public ResponseEntity<ErrorDto> handleClubCloseConditionsNotMetException(
            ClubCloseConditionsNotMetException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, CONDITIONS_NOT_MET_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки дубликат роли у сотрудника в клубе
     */
    @ExceptionHandler(DuplicateEmployeeRoleInClubException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEmployeeRoleInClubException(
            DuplicateEmployeeRoleInClubException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, DUPLICATE_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки дубликат дня-исключения в клубе
     */
    @ExceptionHandler(DuplicateWorkScheduleExceptionInClubException.class)
    public ResponseEntity<ErrorDto> handleDuplicateWorkScheduleExceptionInClubException(
            DuplicateWorkScheduleExceptionInClubException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, DUPLICATE_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки неверных данных расписания работы
     */
    @ExceptionHandler(InvalidWorkScheduleException.class)
    public ResponseEntity<ErrorDto> handleInvalidWorkScheduleException(
            InvalidWorkScheduleException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, CONDITIONS_NOT_MET_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки дубликат цены за количество минут в клубе
     */
    @ExceptionHandler(DuplicatePriceInClubException.class)
    public ResponseEntity<ErrorDto> handleDuplicatePriceInClubException(
            DuplicatePriceInClubException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, DUPLICATE_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки при попытке изменить порядок фотографий
     */
    @ExceptionHandler(PhotoReorderMismatchException.class)
    public ResponseEntity<ErrorDto> handlePhotoReorderMismatchException(
            PhotoReorderMismatchException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, CONDITIONS_NOT_MET_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки дубликат сотрудника в клубе
     */
    @ExceptionHandler(DuplicateEmployeeInClubException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEmployeeInClubException(
            DuplicateEmployeeInClubException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, DUPLICATE_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки, когда клиент при попытке войти не зарегистрирован
     */
    @ExceptionHandler(InvalidPhoneOrPasswordException.class)
    public ResponseEntity<ErrorDto> handleClientWithThisPhoneNotFoundException(
            InvalidPhoneOrPasswordException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, AUTH_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки, когда клиент не найден по номеру телефона
     */
    @ExceptionHandler(ClientWithThisPhoneNotFoundException.class)
    public ResponseEntity<ErrorDto> handleClientWithThisPhoneNotFoundException(
            ClientWithThisPhoneNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, AUTH_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки, когда клиент не найден по номеру телефона
     */
    @ExceptionHandler(EmployeeWithThisPhoneNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEmployeeWithThisPhoneNotFoundException(
            EmployeeWithThisPhoneNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, AUTH_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки сущность не найдена
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ENTITY_NOT_FOUND_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки, когда клиент при попытке зарегистрироваться уже зарегистрирован
     */
    @ExceptionHandler(ClientWithThisPhoneAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleClientWithThisPhoneAlreadyExistsException(
            ClientWithThisPhoneAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, AUTH_ERROR, ex.getMessage(), request);
    }

    /**
     * Обработка ошибки EmployeeWithThisPhoneAlreadyExistsException
     */
    @ExceptionHandler(EmployeeWithThisPhoneAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleEmployeeWithThisPhoneAlreadyExistsException(
            EmployeeWithThisPhoneAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, AUTH_ERROR, ex.getMessage(), request);
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