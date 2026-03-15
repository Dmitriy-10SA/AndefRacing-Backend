package ru.andef.andefracing.backend.network.dtos.report;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO для отчета «Финансовая статистика»
 */
public record FinancialStatisticsDto(
        Integer clubId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        BigDecimal totalRevenue,
        List<DateAndTotalRevenueDto> dateAndTotalRevenues,
        BigDecimal averageReceipt
) {
    public record DateAndTotalRevenueDto(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, BigDecimal revenue) {
    }
}
