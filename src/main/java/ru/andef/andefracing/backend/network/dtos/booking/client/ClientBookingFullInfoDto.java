package ru.andef.andefracing.backend.network.dtos.booking.client;

import lombok.Getter;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.network.dtos.CityDto;
import ru.andef.andefracing.backend.network.dtos.ClubShortDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO для полной информации о бронировании для клиента
 */
@Getter
public class ClientBookingFullInfoDto extends ClientBookingShortDto {
    private final short cntEquipment;
    private final BigDecimal price;
    private final String note;

    public ClientBookingFullInfoDto(
            long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            BookingStatus status,
            ClubShortDto club,
            CityDto city,
            short cntEquipment,
            BigDecimal price,
            String note
    ) {
        super(id, date, startTime, endTime, status, club, city);
        this.cntEquipment = cntEquipment;
        this.price = price;
        this.note = note;
    }
}
