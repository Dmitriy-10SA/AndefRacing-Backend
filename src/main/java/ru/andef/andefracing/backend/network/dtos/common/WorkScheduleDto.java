package ru.andef.andefracing.backend.network.dtos.common;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WorkScheduleDto(
        long id,
        DayOfWeek dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isWorkDay
) {
}
