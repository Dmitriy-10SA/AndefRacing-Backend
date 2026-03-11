package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.projections.BookingStatsAggregateProjection;
import ru.andef.andefracing.backend.data.projections.BookingsPerDayProjection;
import ru.andef.andefracing.backend.data.projections.FinancialStatsAggregateProjection;
import ru.andef.andefracing.backend.data.projections.RevenuePerDayProjection;

import java.time.OffsetDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Для отчета «Cтатистика бронирований». Получаем:
     * общее число бронирований, процент отмен
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT
                        COUNT(*) as bookingsCount,
                        COALESCE(
                            100.0 * SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) / COUNT(*),
                            0
                        ) as cancellationsPercent
                    FROM bookings.booking
                    WHERE club_id = :clubId
                    AND start_datetime < :end
                    AND end_datetime > :start
                    """
    )
    BookingStatsAggregateProjection getBookingStatsAggregate(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end
    );

    /**
     * Для отчета «Cтатистика бронирований». Получаем:
     * число бронирований по дням
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT
                        DATE(start_datetime) as date,
                        COUNT(*) as bookingsCount
                    FROM bookings.booking
                    WHERE club_id = :clubId
                    AND start_datetime < :end
                    AND end_datetime > :start
                    GROUP BY DATE(start_datetime)
                    ORDER BY DATE(start_datetime)
                    """
    )
    List<BookingsPerDayProjection> getBookingsPerDay(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end
    );

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

    /**
     * Получение числа всех предстоящих оплаченных или ожидающих оплаты бронирований в клубе
     */
    @Query(
            value = """
                    SELECT COUNT(b)
                    FROM Booking b
                    WHERE b.club.id = :clubId AND
                    b.endDateTime >= CURRENT_TIMESTAMP AND
                    b.status IN ('PAID', 'PENDING_PAYMENT')"""
    )
    long countUpcomingPaidOrPendingBookings(@Param(value = "clubId") int clubId);
}
