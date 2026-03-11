package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.projections.FinancialStatsAggregateProjection;
import ru.andef.andefracing.backend.data.projections.RevenuePerDayProjection;

import java.time.OffsetDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Для отчета «Финансовая статистика». Получаем:
     * общую выручку, средний чек
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT
                        COALESCE(SUM(price_value), 0) AS totalRevenue,
                        COALESCE(AVG(price_value), 0) AS averageReceipt
                    FROM bookings.booking
                    WHERE club_id = :clubId
                    AND start_datetime < :end
                    AND end_datetime > :start
                    AND status = 'PAID'
                    """
    )
    FinancialStatsAggregateProjection getFinancialStatAggregate(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end
    );

    /**
     * Для отчета «Финансовая статистика». Получаем:
     * выручку по дням
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT 
                        DATE(start_datetime) as date,
                        SUM(price_value) as revenue
                    FROM bookings.booking
                    WHERE club_id = :clubId
                    AND start_datetime < :end
                    AND end_datetime > :start
                    AND status = 'PAID'
                    GROUP BY DATE(start_datetime)
                    ORDER BY DATE(start_datetime)
                    """
    )
    List<RevenuePerDayProjection> getRevenuePerDay(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end
    );
}
