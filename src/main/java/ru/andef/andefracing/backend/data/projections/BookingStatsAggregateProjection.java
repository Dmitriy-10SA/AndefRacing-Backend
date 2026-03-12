package ru.andef.andefracing.backend.data.projections;

import java.math.BigDecimal;

public interface BookingStatsAggregateProjection {
    long getBookingsCount();

    BigDecimal getCancellationsPercent();
}
