package ru.andef.andefracing.backend.network.dtos.search;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO - день из графика работы
 */
public record WorkScheduleDto(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        Boolean isWorkDay
) {
}
