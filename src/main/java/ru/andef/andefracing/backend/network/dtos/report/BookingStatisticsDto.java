package ru.andef.andefracing.backend.network.dtos.report;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO для отчета «Cтатистика бронирований»
 */
public record BookingStatisticsDto(
        Integer clubId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Long bookingsCount,
        BigDecimal cancellationsPercent,
        List<DateAndBookingsCountDto> dateAndBookingsCountDtoList
) {
    public record DateAndBookingsCountDto(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Long bookingsCount) {
    }
}
