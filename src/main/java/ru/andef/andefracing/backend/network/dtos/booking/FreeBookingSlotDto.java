package ru.andef.andefracing.backend.network.dtos.booking;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * DTO свободного слота бронирования
 */
public record FreeBookingSlotDto(
        @NotNull(message = "Необходимо передать начало слота") OffsetDateTime startDateTime,
        @NotNull(message = "Необходимо передать конец слота") OffsetDateTime endDateTime
) {
}
