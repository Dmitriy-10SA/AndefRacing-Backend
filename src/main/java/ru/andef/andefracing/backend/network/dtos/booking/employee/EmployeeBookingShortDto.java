package ru.andef.andefracing.backend.network.dtos.booking.employee;

import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.network.dtos.booking.BookingShortDto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dto для краткой информации о бронировании для сотрудника
 */
public class EmployeeBookingShortDto extends BookingShortDto {
    public EmployeeBookingShortDto(
            long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            BookingStatus status
    ) {
        super(id, date, startTime, endTime, status);
    }
}
