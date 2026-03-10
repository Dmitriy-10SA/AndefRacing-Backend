package ru.andef.andefracing.backend.network.dtos.management;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO - день-исключение в графике работы
 */
public record WorkScheduleExceptionDto(
        long id,
        LocalDate date,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isWorkDay,
        String description
) {
}
