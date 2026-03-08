package ru.andef.andefracing.backend.network.dtos.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO для получения свободных слотов бронирования
 */
public record FreeBookingSlotsRequest(
        @Min(1) short durationMinutes,
        @Min(1) short cntEquipment,
        @NotNull LocalDate date
) {
}
