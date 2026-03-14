package ru.andef.andefracing.backend.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - неверный диапазон дат
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException() {
        super("Неверный диапазон дат - дата начала должна быть меньше даты конца");
    }
}
