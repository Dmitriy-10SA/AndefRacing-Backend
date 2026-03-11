package ru.andef.andefracing.backend.data.projections;

import java.math.BigDecimal;

public interface FinancialStatsAggregateProjection {
    BigDecimal getTotalRevenue();

    BigDecimal getAverageReceipt();
}
