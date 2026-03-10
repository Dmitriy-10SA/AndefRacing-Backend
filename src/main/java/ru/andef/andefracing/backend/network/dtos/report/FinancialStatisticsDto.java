package ru.andef.andefracing.backend.network.dtos.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO для отчета «Финансовая статистика»
 */
public record FinancialStatisticsDto(
        int clubId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalRevenue,
        List<DateAndTotalRevenueDto> dateAndTotalRevenues,
        BigDecimal averageReceipt
) {
    public record DateAndTotalRevenueDto(LocalDate date, BigDecimal revenue) {
    }
}
