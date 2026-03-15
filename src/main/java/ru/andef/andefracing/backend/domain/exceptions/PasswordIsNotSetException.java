package ru.andef.andefracing.backend.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - пароль не задан
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PasswordIsNotSetException extends RuntimeException {
    public PasswordIsNotSetException(String message) {
        super(message);
    }
}
