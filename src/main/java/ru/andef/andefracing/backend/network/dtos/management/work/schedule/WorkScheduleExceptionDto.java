package ru.andef.andefracing.backend.network.dtos.management.work.schedule;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO - день-исключение в графике работы
 */
public record WorkScheduleExceptionDto(
        Long id,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        LocalTime openTime,
        LocalTime closeTime,
        @NotNull Boolean isWorkDay,
        String description
) {
}
