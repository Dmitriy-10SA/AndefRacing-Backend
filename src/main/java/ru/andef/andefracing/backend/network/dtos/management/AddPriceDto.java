package ru.andef.andefracing.backend.network.dtos.management;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO для добавления цены за количество минут
 */
public record AddPriceDto(
        @Min(value = 15, message = "Кол-во минут должно быть >= 15 минут")
        short durationMinutes,
        @NotNull(message = "Необходимо указать стоимость")
        @Min(value = 1, message = "Стоимость должна быть >= 1")
        BigDecimal value
) {
}
