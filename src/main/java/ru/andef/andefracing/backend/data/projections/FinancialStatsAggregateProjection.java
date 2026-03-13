package ru.andef.andefracing.backend.data.projections;

import java.math.BigDecimal;

/**
 * Проекция для агрегированной финансовой статистики
 */
public interface FinancialStatsAggregateProjection {
    BigDecimal getTotalRevenue();

    BigDecimal getAverageReceipt();
}
