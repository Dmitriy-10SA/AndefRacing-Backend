package ru.andef.andefracing.backend.network.dtos.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DTO для создания бронирования
 */
@Getter
@RequiredArgsConstructor
public abstract class MakeBookingDto {
    @Min(value = 1, message = "Кол-во оборудования для бронирования должно быть >= 1")
    private final short cntEquipment;
    @NotNull(message = "Необходимо передать слот")
    @Valid
    private final FreeBookingSlotDto slot;
    private final String note;
}