package ru.andef.andefracing.backend.domain.exceptions.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - неверные данные в слоте бронирования
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBookingSlotException extends RuntimeException {
    public InvalidBookingSlotException(String message) {
        super(message);
    }
}
