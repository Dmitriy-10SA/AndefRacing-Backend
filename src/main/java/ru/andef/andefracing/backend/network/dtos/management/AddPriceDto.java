package ru.andef.andefracing.backend.network.dtos.management;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddPriceDto(
        @Min(1) short durationMinutes,
        @NotNull @Min(1) BigDecimal value
) {
}
