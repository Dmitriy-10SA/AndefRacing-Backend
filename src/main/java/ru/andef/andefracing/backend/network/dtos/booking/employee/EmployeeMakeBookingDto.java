package ru.andef.andefracing.backend.network.dtos.booking.employee;

import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.MakeBookingDto;

/**
 * DTO для создания бронирования сотрудником
 */
public class EmployeeMakeBookingDto extends MakeBookingDto {
    public EmployeeMakeBookingDto(short cntEquipment, FreeBookingSlotDto slot, String note) {
        super(cntEquipment, slot, note);
    }
}
