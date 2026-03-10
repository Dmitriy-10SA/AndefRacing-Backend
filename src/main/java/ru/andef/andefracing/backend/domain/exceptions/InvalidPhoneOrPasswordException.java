package ru.andef.andefracing.backend.domain.exceptions;

/**
 * Ошибка - неверный телефон или пароль
 */
public class InvalidPhoneOrPasswordException extends RuntimeException {
    public InvalidPhoneOrPasswordException() {
        super("Неверный номер телефона или пароль");
    }
}