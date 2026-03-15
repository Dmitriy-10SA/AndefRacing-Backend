package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка открытия клуба из-за несоответствия условиям открытия
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ClubOpenConditionsNotMetException extends RuntimeException {
    private final int id;

    public ClubOpenConditionsNotMetException(int id) {
        super(
                "Нельзя открыть клуб с id " + id +
                        ", так как не выполнены все необходимые для этого условия, а именно:" +
                        "кол-во фотографий >= 1, кол-во цен >= 1, активных игр >= 1 " +
                        "и написан стандартный график работы на всю неделю"
        );
        this.id = id;
    }
}
