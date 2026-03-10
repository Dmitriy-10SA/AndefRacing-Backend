package ru.andef.andefracing.backend.network.dtos.management.work.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO для изменения основного графика работы
 */
public record UpdateWorkScheduleDto(
        @NotNull(message = "Необходимо передать дату") DayOfWeek dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isWorkDay
) {
}
