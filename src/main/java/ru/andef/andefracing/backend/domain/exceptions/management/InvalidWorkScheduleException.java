package ru.andef.andefracing.backend.domain.exceptions.management;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - неверные данные расписания работы
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWorkScheduleException extends RuntimeException {
    public InvalidWorkScheduleException(String message) {
        super(message);
    }
}
