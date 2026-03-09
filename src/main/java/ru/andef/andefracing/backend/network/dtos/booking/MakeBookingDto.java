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
    @Min(1)
    private final short cntEquipment;
    @NotNull
    @Valid
    private final FreeBookingSlotDto slot;
    private final String note;
}