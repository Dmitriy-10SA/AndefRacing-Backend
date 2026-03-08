package ru.andef.andefracing.backend.network.dtos.booking.client;

import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.MakeBookingDto;

/**
 * DTO для создания бронирования клиентом
 */
public class ClientMakeBookingDto extends MakeBookingDto {
    public ClientMakeBookingDto(short durationMinutes, short cntEquipment, FreeBookingSlotDto slot, String note) {
        super(durationMinutes, cntEquipment, slot, note);
    }
}
