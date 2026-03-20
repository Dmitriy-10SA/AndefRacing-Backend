package ru.andef.andefracing.backend.network.dtos.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * Dto для краткой информации о бронировании
 */
@Getter
@RequiredArgsConstructor
public abstract class BookingShortDto {
    private final Long id;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final BookingStatus status;
}
