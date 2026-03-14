package ru.andef.andefracing.backend.network.dtos.management.work.schedule;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO для добавления дня-исключения в расписании работы
 */
public record AddWorkScheduleExceptionDto(
        @NotNull(message = "Необходимо передать дату") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isWorkDay,
        String description
) {
}
