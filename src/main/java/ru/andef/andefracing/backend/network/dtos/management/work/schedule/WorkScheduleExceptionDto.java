package ru.andef.andefracing.backend.network.dtos.management.work.schedule;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO - день-исключение в графике работы
 */
public record WorkScheduleExceptionDto(
        long id,
        @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isWorkDay,
        String description
) {
}
