package ru.andef.andefracing.backend.network.dtos.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO для отчета «Cтатистика бронирований»
 */
public record BookingStatisticsDto(
        int clubId,
        LocalDate startDate,
        LocalDate endDate,
        long bookingsCount,
        BigDecimal cancellationsPercent,
        List<DateAndBookingsCountDto> dateAndBookingsCountDtoList
) {
    public record DateAndBookingsCountDto(LocalDate date, long bookingsCount) {
    }
}
