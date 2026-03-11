package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - дубликат цены в клубе за количество минут
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatePriceInClubException extends RuntimeException {
    private final int id;
    private final short durationMinutes;

    public DuplicatePriceInClubException(int id, short durationMinutes) {
        super("В клубе с id " + id + " уже есть стоимость за " + durationMinutes + " минут");
        this.id = id;
        this.durationMinutes = durationMinutes;
    }
}
