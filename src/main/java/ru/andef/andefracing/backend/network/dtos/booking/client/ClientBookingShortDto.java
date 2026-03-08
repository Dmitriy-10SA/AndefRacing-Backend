package ru.andef.andefracing.backend.network.dtos.booking.client;

import lombok.Getter;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.network.dtos.CityDto;
import ru.andef.andefracing.backend.network.dtos.ClubShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.BookingShortDto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dto для краткой информации о бронировании для клиента
 */
@Getter
public class ClientBookingShortDto extends BookingShortDto {
    private final ClubShortDto club;
    private final CityDto city;

    public ClientBookingShortDto(
            long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            BookingStatus status,
            ClubShortDto club,
            CityDto city
    ) {
        super(id, date, startTime, endTime, status);
        this.club = club;
        this.city = city;
    }
}
