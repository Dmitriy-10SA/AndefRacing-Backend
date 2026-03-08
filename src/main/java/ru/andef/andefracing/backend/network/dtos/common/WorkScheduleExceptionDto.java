package ru.andef.andefracing.backend.network.dtos.common;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkScheduleExceptionDto(
        long id,
        LocalDate date,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isWorkDay,
        String description
) {
}
