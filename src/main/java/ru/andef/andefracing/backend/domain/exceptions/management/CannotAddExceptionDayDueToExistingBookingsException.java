package ru.andef.andefracing.backend.domain.exceptions.management;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - попытка добавления дня исключения, когда уже есть существующие бронирования
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CannotAddExceptionDayDueToExistingBookingsException extends RuntimeException {
    public CannotAddExceptionDayDueToExistingBookingsException() {
        super("Нельзя добавить день-исключение при наличии бронирований");
    }
}
