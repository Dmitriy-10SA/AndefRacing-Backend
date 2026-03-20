package ru.andef.andefracing.backend.network.dtos.booking.client;

import lombok.Getter;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.CityDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для полной информации о бронировании для клиента
 */
@Getter
public class ClientBookingFullInfoDto extends ClientBookingShortDto {
    private final Short cntEquipment;
    private final BigDecimal price;
    private final String note;

    public ClientBookingFullInfoDto(
            long id,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BookingStatus status,
            ClubShortDto club,
            CityDto city,
            Short cntEquipment,
            BigDecimal price,
            String note
    ) {
        super(id, startDateTime, endDateTime, status, club, city);
        this.cntEquipment = cntEquipment;
        this.price = price;
        this.note = note;
    }
}
