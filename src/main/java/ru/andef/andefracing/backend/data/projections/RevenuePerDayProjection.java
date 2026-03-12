package ru.andef.andefracing.backend.data.projections;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Проекция для дохода по дням.
 */
public interface RevenuePerDayProjection {
    LocalDate getDate();

    BigDecimal getRevenue();
}
