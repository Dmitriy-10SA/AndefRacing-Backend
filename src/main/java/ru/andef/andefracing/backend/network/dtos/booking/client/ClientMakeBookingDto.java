package ru.andef.andefracing.backend.network.dtos.booking.client;

import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.MakeBookingDto;

/**
 * DTO для создания бронирования клиентом
 */
public class ClientMakeBookingDto extends MakeBookingDto {
    public ClientMakeBookingDto(FreeBookingSlotDto slot, String note) {
        super(slot, note);
    }
}
