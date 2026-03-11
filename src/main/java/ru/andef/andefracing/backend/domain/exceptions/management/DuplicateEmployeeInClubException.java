package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - сотрудник уже есть в клубе
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmployeeInClubException extends RuntimeException {
    private final long id;

    public DuplicateEmployeeInClubException(long id) {
        super("Сотрудник с id " + id + " уже есть в клубе");
        this.id = id;
    }
}
