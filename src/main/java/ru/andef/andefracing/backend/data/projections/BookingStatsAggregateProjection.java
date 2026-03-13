package ru.andef.andefracing.backend.data.projections;

import java.math.BigDecimal;

/**
 * Проекция для агрегированной статистики бронирований
 */
public interface BookingStatsAggregateProjection {
    long getBookingsCount();

    BigDecimal getCancellationsPercent();
}
