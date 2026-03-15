package ru.andef.andefracing.backend.network.dtos.search;

import java.math.BigDecimal;

/**
 * DTO - цена за количество минут
 */
public record PriceDto(
        Long id,
        Short durationMinutes,
        BigDecimal value
) {
}
