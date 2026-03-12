package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

/**
 * Ошибка - день-исключение уже есть в клубе
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateWorkScheduleExceptionInClubException extends RuntimeException {
    private final LocalDate date;

    public DuplicateWorkScheduleExceptionInClubException(LocalDate date) {
        super("День-исключение с датой " + date + " уже есть в клубе");
        this.date = date;
    }
}
