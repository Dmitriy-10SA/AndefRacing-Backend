package ru.andef.andefracing.backend.network.controllers.booking;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.BookingService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotsRequestDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientMakeBookingDto;
import ru.andef.andefracing.backend.network.security.JwtFilter;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Booking - клиент")
@RestController
@RequestMapping(ApiPaths.BOOKINGS_CLIENT)
@Validated
@RequiredArgsConstructor
public class ClientBookingController {
    private final BookingService bookingService;

    /**
     * Получение доступных слотов для бронирования в клубе
     */
    @GetMapping(path = "/free-slots/{clubId}", version = ApiVersions.V1)
    public ResponseEntity<List<FreeBookingSlotDto>> getFreeBookingSlotsInClub(
            @PathVariable int clubId,
            @RequestBody @Valid FreeBookingSlotsRequestDto freeBookingSlotsRequestDto
    ) {
        List<FreeBookingSlotDto> freeBookingSlots = bookingService
                .getFreeBookingSlotsInClub(clubId, freeBookingSlotsRequestDto);
        return ResponseEntity.ok(freeBookingSlots);
    }

    /**
     * Сделать бронирование
     */
    @PostMapping(path = "/make-booking/{clubId}", version = ApiVersions.V1)
    public ResponseEntity<Void> makeBooking(
            @PathVariable int clubId,
            @RequestBody @Valid ClientMakeBookingDto makeBookingDto,
            Authentication authentication
    ) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bookingService.makeClientBooking(principal.id(), clubId, makeBookingDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение списка всех бронирований за диапазон дат
     */
    @GetMapping(version = ApiVersions.V1)
    public ResponseEntity<List<ClientBookingShortDto>> getBookings(
            @RequestParam(name = "startDate") @NotNull LocalDate startDate,
            @RequestParam(name = "endDate") @NotNull LocalDate endDate,
            Authentication authentication
    ) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ClientBookingShortDto> bookings = bookingService
                .getAllClientBookings(principal.id(), startDate, endDate);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Просмотр полной информации о бронировании
     */
    @GetMapping(path = "/{clubId}/{bookingId}", version = ApiVersions.V1)
    public ResponseEntity<ClientBookingFullInfoDto> getFullBookingInfo(
            @PathVariable int clubId,
            @PathVariable long bookingId,
            Authentication authentication
    ) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ClientBookingFullInfoDto clientBookingFullInfoDto = bookingService
                .getBookingFullInfoForClient(principal.id(), clubId, bookingId);
        return ResponseEntity.ok(clientBookingFullInfoDto);
    }
}