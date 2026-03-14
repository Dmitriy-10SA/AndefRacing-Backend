package ru.andef.andefracing.backend.network.dtos.report;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO для отчета «Cтатистика бронирований»
 */
public record BookingStatisticsDto(
        int clubId,
        @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
        long bookingsCount,
        BigDecimal cancellationsPercent,
        List<DateAndBookingsCountDto> dateAndBookingsCountDtoList
) {
    public record DateAndBookingsCountDto(@DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date, long bookingsCount) {
    }
}
