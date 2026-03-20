package ru.andef.andefracing.backend.domain.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - пользователь системы не найден по токену
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotFoundFromTokenException extends RuntimeException {
    public UserNotFoundFromTokenException() {
        super();
    }
}
