package ru.andef.andefracing.backend.network.dtos.management.work.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO для добавления дня-исключения в расписании работы
 */
public record AddWorkScheduleExceptionDto(
        @NotNull LocalDate date,
        @NotNull LocalTime openTime,
        @NotNull LocalTime closeTime,
        boolean isWorkDay,
        String description
) {
}
