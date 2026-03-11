package ru.andef.andefracing.backend.data.projections;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RevenuePerDayProjection {
    LocalDate getDate();

    BigDecimal getRevenue();
}
