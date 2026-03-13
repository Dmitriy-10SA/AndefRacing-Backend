package ru.andef.andefracing.backend.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка блокировки
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class BlockedException extends RuntimeException {
    public BlockedException(String message) {
        super(message);
    }
}
