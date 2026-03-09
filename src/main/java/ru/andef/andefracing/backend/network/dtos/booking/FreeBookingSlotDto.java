package ru.andef.andefracing.backend.network.dtos.booking;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * DTO свободного слота бронирования
 */
public record FreeBookingSlotDto(
        @NotNull OffsetDateTime startDateTime,
        @NotNull OffsetDateTime endDateTime
) {
}
