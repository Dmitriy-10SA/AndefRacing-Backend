package ru.andef.andefracing.backend.data.projections;

import java.time.LocalDate;

/**
 * Проекция для подсчета количества бронирований по дням
 */
public interface BookingsPerDayProjection {
    LocalDate getDate();

    long getBookingsCount();
}
