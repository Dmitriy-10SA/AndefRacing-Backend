package ru.andef.andefracing.backend.network.dtos.booking.employee;

import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.MakeBookingDto;

public class EmployeeMakeBookingDto extends MakeBookingDto {
    public EmployeeMakeBookingDto(short durationMinutes, short cntEquipment, FreeBookingSlotDto slot, String note) {
        super(durationMinutes, cntEquipment, slot, note);
    }
}
