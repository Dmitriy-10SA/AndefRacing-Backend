package ru.andef.andefracing.backend.domain.exceptions.profile.client;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - клуб уже добавлен в избранные
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateFavoriteClubException extends RuntimeException {
    private final int id;

    public DuplicateFavoriteClubException(int id) {
        super("Клуб с id " + id + " уже добавлен в избранное");
        this.id = id;
    }
}
