package ru.andef.andefracing.backend.network.controllers.booking;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeMakeBookingDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Tag(name = ApiTags.EMPLOYEE_BOOKING)
@RestController
@RequestMapping(ApiPaths.BOOKINGS_EMPLOYEE)
@Validated
@RequiredArgsConstructor
public class EmployeeBookingController {
    private final BookingSearchService bookingSearchService;
    private final BookingManagementService bookingManagementService;

    /**
     * Получение доступных слотов для бронирования в клубе
     */
    @GetMapping(path = "/free-slots", version = ApiVersions.V1)
    public ResponseEntity<List<FreeBookingSlotDto>> getFreeBookingSlotsInClub(
            @RequestBody @Valid FreeBookingSlotsRequestDto freeBookingSlotsRequestDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<FreeBookingSlotDto> freeBookingSlots = bookingSearchService
                .getFreeBookingSlotsInClub(principal.clubId(), freeBookingSlotsRequestDto);
        return ResponseEntity.ok(freeBookingSlots);
    }

    /**
     * Подтверждение оплаты бронирования
     */
    @PatchMapping(path = "/confirm-booking-payment/{bookingId}", version = ApiVersions.V1)
    public ResponseEntity<Void> confirmBookingPayment(@PathVariable long bookingId, Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bookingManagementService.confirmBookingPaymentByEmployee(principal.id(), principal.clubId(), bookingId);
        return ResponseEntity.ok().build();
    }

    /**
     * Сделать бронирование
     */
    @PostMapping(path = "/make-booking", version = ApiVersions.V1)
    public ResponseEntity<Void> makeBooking(
            @RequestBody @Valid EmployeeMakeBookingDto makeBookingDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bookingManagementService.makeEmployeeBooking(principal.id(), principal.clubId(), makeBookingDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Отмена бронирования
     */
    @PatchMapping(path = "/cancel/{bookingId}", version = ApiVersions.V1)
    public ResponseEntity<Void> cancelBooking(@PathVariable long bookingId, Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bookingManagementService.cancelBookingByEmployee(principal.id(), principal.clubId(), bookingId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение списка всех бронирований за диапазон дат и по номеру телефона клиента (номер телефона опционален)
     */
    @GetMapping(version = ApiVersions.V1)
    public ResponseEntity<List<EmployeeBookingShortDto>> getBookings(
            @RequestParam("startDate") @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam("endDate") @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
            @RequestParam(name = "clientPhone", required = false)
            @NotBlank(message = "Номер телефона должен быть заполнен")
            @Pattern(
                    regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
                    message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
            )
            String clientPhone,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<EmployeeBookingShortDto> bookings = bookingSearchService.getBookingsForEmployee(
                principal.id(),
                principal.clubId(),
                startDate,
                endDate,
                clientPhone == null ? Optional.empty() : Optional.of(clientPhone)
        );
        return ResponseEntity.ok(bookings);
    }

    /**
     * Просмотр полной информации о бронировании
     */
    @GetMapping(path = "/{bookingId}", version = ApiVersions.V1)
    public ResponseEntity<EmployeeBookingFullInfoDto> getFullBookingInfo(
            @PathVariable long bookingId,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        EmployeeBookingFullInfoDto employeeBookingFullInfoDto = bookingSearchService
                .getBookingFullInfoForEmployee(principal.id(), principal.clubId(), bookingId);
        return ResponseEntity.ok(employeeBookingFullInfoDto);
    }
}