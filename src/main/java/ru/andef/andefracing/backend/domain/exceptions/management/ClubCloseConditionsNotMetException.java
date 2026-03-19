package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка закрытия клуба из-за несоответствия условиям закрытия
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ClubCloseConditionsNotMetException extends RuntimeException {
    private final int id;

    public ClubCloseConditionsNotMetException(int id) {
        super(
                "Нельзя закрыть клуб, так как не выполнены все необходимые для этого условия, а именно:" +
                        " есть оплаченные или ожидающие оплаты бронирования"
        );
        this.id = id;
    }
}
