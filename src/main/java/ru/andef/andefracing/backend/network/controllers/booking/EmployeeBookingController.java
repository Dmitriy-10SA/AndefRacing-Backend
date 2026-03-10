package ru.andef.andefracing.backend.network.controllers.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotsRequestDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeMakeBookingDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.BOOKINGS_EMPLOYEE)
@Validated
public class EmployeeBookingController {
    /**
     * Получение доступных слотов для бронирования
     */
    @GetMapping("/free-slots")
    public ResponseEntity<List<FreeBookingSlotDto>> getFreeBookingSlots(
            @PathVariable int clubId,
            @RequestBody @Valid FreeBookingSlotsRequestDto freeBookingSlotsRequestDto
    ) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Подтверждение оплаты бронирования
     */
    @PatchMapping("/confirm-booking-payment")
    public ResponseEntity<Void> confirmBookingPayment(@PathVariable int clubId) {
        // TODO
        return null;
    }

    /**
     * Сделать бронирование
     */
    @PostMapping("/make-booking")
    public ResponseEntity<Void> makeBooking(
            @PathVariable int clubId,
            @RequestBody @Valid EmployeeMakeBookingDto makeBookingDto
    ) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Отмена бронирования
     */
    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable int clubId, @PathVariable long bookingId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение списка всех бронирований за диапазон дат и по номеру телефона клиента (номер телефона опционален)
     */
    @GetMapping
    public ResponseEntity<List<EmployeeBookingShortDto>> getBookings(
            @PathVariable int clubId,
            @RequestParam("startDate") @NotNull LocalDate startDate,
            @RequestParam("endDate") @NotNull LocalDate endDate,
            @RequestParam(name = "clientPhone", required = false)
            @NotBlank(message = "Номер телефона должен быть заполнен")
            @Pattern(
                    regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
                    message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
            )
            String clientPhone
    ) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Просмотр полной информации о бронировании
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<EmployeeBookingFullInfoDto> getFullBookingInfo(
            @PathVariable int clubId,
            @PathVariable long bookingId
    ) {
        // TODO
        return ResponseEntity.ok(null);
    }
}