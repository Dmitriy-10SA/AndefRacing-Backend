package ru.andef.andefracing.backend.network.dtos.booking;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO свободного слота бронирования
 */
public record FreeBookingSlotDto(
        @NotNull(message = "Необходимо передать начало слота")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //yyyy-MM-dd'T'HH:mm:ss
        LocalDateTime startDateTime,
        @NotNull(message = "Необходимо передать конец слота")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //2024-01-15T14:30:00
        LocalDateTime endDateTime
) {
}
