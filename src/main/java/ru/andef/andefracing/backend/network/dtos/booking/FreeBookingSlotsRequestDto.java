package ru.andef.andefracing.backend.network.dtos.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO для получения свободных слотов бронирования
 */
public record FreeBookingSlotsRequestDto(
        @NotNull@Min(value = 1, message = "Длительность бронирования должна быть >= 1 минуты") Short durationMinutes,
        @NotNull @Min(value = 1, message = "Кол-во оборудования для бронирования должно быть >= 1") Short cntEquipment,
        @NotNull(message = "Необходимо передать дату") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date
) {
}
