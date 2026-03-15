package ru.andef.andefracing.backend.network.dtos.booking.employee;

import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.MakeBookingDto;

import java.math.BigDecimal;

/**
 * DTO для создания бронирования сотрудником
 */
public class EmployeeMakeBookingDto extends MakeBookingDto {
    public EmployeeMakeBookingDto(Short cntEquipment, BigDecimal price, FreeBookingSlotDto slot, String note) {
        super(cntEquipment, price, slot, note);
    }
}
