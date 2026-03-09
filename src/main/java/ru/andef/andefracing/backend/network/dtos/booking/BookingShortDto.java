package ru.andef.andefracing.backend.network.dtos.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dto для краткой информации о бронировании
 */
@Getter
@RequiredArgsConstructor
public abstract class BookingShortDto {
    private final long id;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final BookingStatus status;
}
