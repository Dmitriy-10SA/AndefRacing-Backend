package ru.andef.andefracing.backend.data.projections;

import java.time.LocalDate;

public interface BookingsPerDayProjection {
    LocalDate getDate();

    long getBookingsCount();
}
