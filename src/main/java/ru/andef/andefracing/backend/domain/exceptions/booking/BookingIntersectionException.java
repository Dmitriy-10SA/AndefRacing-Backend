package ru.andef.andefracing.backend.domain.exceptions.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - недостаток симуляторов для бронирования
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BookingIntersectionException extends RuntimeException {
    public BookingIntersectionException() {
        super("Недостаточно симуляторов для бронирования");
    }
}
