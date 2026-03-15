package ru.andef.andefracing.backend.network.dtos.booking.client;

import lombok.Getter;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.network.dtos.booking.BookingShortDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.CityDto;

import java.time.OffsetDateTime;

/**
 * Dto для краткой информации о бронировании для клиента
 */
@Getter
public class ClientBookingShortDto extends BookingShortDto {
    private final ClubShortDto club;
    private final CityDto city;

    public ClientBookingShortDto(
            long id,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            BookingStatus status,
            ClubShortDto club,
            CityDto city
    ) {
        super(id, startDateTime, endDateTime, status);
        this.club = club;
        this.city = city;
    }
}
