package ru.andef.andefracing.backend.network.dtos.booking.employee;

import lombok.Getter;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.network.dtos.common.ClientDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO для полной информации о бронировании для сотрудника
 */
@Getter
public class EmployeeBookingFullInfoDto extends EmployeeBookingShortDto {
    private final short cntEquipment;
    private final BigDecimal price;
    private final String note;
    private final ClientDto client;

    public EmployeeBookingFullInfoDto(
            long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            BookingStatus status,
            short cntEquipment,
            BigDecimal price,
            String note,
            ClientDto client
    ) {
        super(id, date, startTime, endTime, status);
        this.cntEquipment = cntEquipment;
        this.price = price;
        this.note = note;
        this.client = client;
    }
}
