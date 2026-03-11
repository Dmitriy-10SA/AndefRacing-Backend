package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - игра уже есть в клубе
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateGameInClubException extends RuntimeException {
    private final short id;

    public DuplicateGameInClubException(short gameId) {
        super("Игра с id " + gameId + " уже есть в клубе");
        this.id = gameId;
    }
}
