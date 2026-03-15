package ru.andef.andefracing.backend.network.dtos.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;

import java.time.OffsetDateTime;

/**
 * Dto для краткой информации о бронировании
 */
@Getter
@RequiredArgsConstructor
public abstract class BookingShortDto {
    private final Long id;
    private final OffsetDateTime startDateTime;
    private final OffsetDateTime endDateTime;
    private final BookingStatus status;
}
