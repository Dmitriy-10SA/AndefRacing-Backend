package ru.andef.andefracing.backend.network.controllers.booking;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.booking.BookingManagementService;
import ru.andef.andefracing.backend.domain.services.booking.BookingSearchService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiTags;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotsRequestDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientMakeBookingDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.PagedClientBookingShortListDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Tag(name = ApiTags.CLIENT_BOOKING)
@RestController
@RequestMapping(ApiPaths.BOOKINGS_CLIENT)
@Validated
@RequiredArgsConstructor
public class ClientBookingController {
    private final BookingSearchService bookingSearchService;
    private final BookingManagementService bookingManagementService;

    /**
     * Получение доступных слотов для бронирования в клубе
     */
    @GetMapping(path = "/free-slots/{clubId}", version = ApiVersions.V1)
    public ResponseEntity<List<FreeBookingSlotDto>> getFreeBookingSlotsInClub(
            @PathVariable int clubId,
            @NotNull
            @Min(value = 15, message = "Длительность бронирования должна быть >= 15 минут")
            Short durationMinutes,
            @NotNull
            @Min(value = 1, message = "Кол-во оборудования для бронирования должно быть >= 1")
            Short cntEquipment,
            @NotNull(message = "Необходимо передать дату")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @NotNull(message = "Необходимо передать время")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime userCurrentTime
    ) {
        FreeBookingSlotsRequestDto freeBookingSlotsRequestDto = new FreeBookingSlotsRequestDto(
                durationMinutes,
                cntEquipment,
                date
        );
        List<FreeBookingSlotDto> freeBookingSlots = bookingSearchService
                .getFreeBookingSlotsInClub(clubId, freeBookingSlotsRequestDto, userCurrentTime);
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
        bookingManagementService.makeClientBooking(principal.id(), clubId, makeBookingDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение списка всех бронирований за диапазон дат с пагинацией
     */
    @GetMapping(version = ApiVersions.V1)
    public ResponseEntity<PagedClientBookingShortListDto> getBookings(
            @RequestParam(name = "startDate")
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(name = "endDate")
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam @NotNull @Min(value = 0) Integer pageNumber,
            @RequestParam @NotNull @Min(value = 1) @Max(value = 100) Integer pageSize,
            Authentication authentication
    ) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PagedClientBookingShortListDto bookings = bookingSearchService
                .getAllClientBookingsPaged(principal.id(), startDate, endDate, pageNumber, pageSize);
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
        ClientBookingFullInfoDto clientBookingFullInfoDto = bookingSearchService
                .getBookingFullInfoForClient(principal.id(), clubId, bookingId);
        return ResponseEntity.ok(clientBookingFullInfoDto);
    }
}