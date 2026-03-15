package ru.andef.andefracing.backend.network.dtos.booking.employee;

import lombok.Getter;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.network.dtos.booking.ClientDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO для полной информации о бронировании для сотрудника
 */
@Getter
public class EmployeeBookingFullInfoDto extends EmployeeBookingShortDto {
    private final Short cntEquipment;
    private final BigDecimal price;
    private final String note;
    private final ClientDto client;

    public EmployeeBookingFullInfoDto(
            Long id,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            BookingStatus status,
            Short cntEquipment,
            BigDecimal price,
            String note,
            ClientDto client
    ) {
        super(id, startDateTime, endDateTime, status);
        this.cntEquipment = cntEquipment;
        this.price = price;
        this.note = note;
        this.client = client;
    }
}
