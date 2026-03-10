package ru.andef.andefracing.backend.network.controllers.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotsRequestDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientMakeBookingDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.BOOKINGS_CLIENT)
@Validated
public class ClientBookingController {
    /**
     * Получение доступных слотов для бронирования
     */
    @GetMapping("/free-slots/{clubId}")
    public ResponseEntity<List<FreeBookingSlotDto>> getFreeBookingSlots(
            @PathVariable int clubId,
            @RequestBody @Valid FreeBookingSlotsRequestDto freeBookingSlotsRequestDto
    ) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Сделать бронирование
     */
    @PostMapping("/make-booking/{clubId}")
    public ResponseEntity<Void> makeBooking(
            @PathVariable int clubId,
            @RequestBody @Valid ClientMakeBookingDto makeBookingDto
    ) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение списка всех бронирований за диапазон дат
     */
    @GetMapping
    public ResponseEntity<List<ClientBookingShortDto>> getBookings(
            @RequestParam(name = "startDate") @NotNull LocalDate startDate,
            @RequestParam(name = "endDate") @NotNull LocalDate endDate
    ) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Просмотр полной информации о бронировании
     */
    @GetMapping("/{clubId}/{bookingId}")
    public ResponseEntity<ClientBookingFullInfoDto> getFullBookingInfo(
            @PathVariable int clubId,
            @PathVariable long bookingId
    ) {
        // TODO
        return ResponseEntity.ok(null);
    }
}