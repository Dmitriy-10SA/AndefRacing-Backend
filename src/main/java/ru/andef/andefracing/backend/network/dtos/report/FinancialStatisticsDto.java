package ru.andef.andefracing.backend.network.dtos.report;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO для отчета «Финансовая статистика»
 */
public record FinancialStatisticsDto(
        int clubId,
        @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
        BigDecimal totalRevenue,
        List<DateAndTotalRevenueDto> dateAndTotalRevenues,
        BigDecimal averageReceipt
) {
    public record DateAndTotalRevenueDto(@DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date, BigDecimal revenue) {
    }
}
