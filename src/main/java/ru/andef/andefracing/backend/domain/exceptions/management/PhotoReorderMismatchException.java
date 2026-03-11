package ru.andef.andefracing.backend.domain.exceptions.management;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка при попытке изменить порядок фотографий
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PhotoReorderMismatchException extends RuntimeException {
    public PhotoReorderMismatchException(String message) {
        super(message);
    }
}
