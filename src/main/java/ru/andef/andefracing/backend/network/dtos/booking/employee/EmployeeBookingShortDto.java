package ru.andef.andefracing.backend.network.dtos.booking.employee;

import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.network.dtos.booking.BookingShortDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

/**
 * Dto для краткой информации о бронировании для сотрудника
 */
public class EmployeeBookingShortDto extends BookingShortDto {
    public EmployeeBookingShortDto(
            long id,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            BookingStatus status
    ) {
        super(id, startDateTime, endDateTime, status);
    }
}
