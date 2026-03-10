package ru.andef.andefracing.backend.domain.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - неверный телефон или пароль
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidPhoneOrPasswordException extends RuntimeException {
    public InvalidPhoneOrPasswordException() {
        super("Неверный номер телефона или пароль");
    }
}