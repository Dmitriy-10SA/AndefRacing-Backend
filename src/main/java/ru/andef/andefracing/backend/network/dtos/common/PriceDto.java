package ru.andef.andefracing.backend.network.dtos.common;

import java.math.BigDecimal;

public record PriceDto(
        long id,
        short durationMinutes,
        BigDecimal value
) {
}
