package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.projections.BookingStatsAggregateProjection;
import ru.andef.andefracing.backend.data.projections.BookingsPerDayProjection;
import ru.andef.andefracing.backend.data.projections.FinancialStatsAggregateProjection;
import ru.andef.andefracing.backend.data.projections.RevenuePerDayProjection;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
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
                        CAST(start_datetime AS DATE) as date,
                        COUNT(*) as bookingsCount
                    FROM bookings.booking
                    WHERE club_id = :clubId
                    AND start_datetime < :end
                    AND end_datetime > :start
                    GROUP BY CAST(start_datetime AS DATE)
                    ORDER BY CAST(start_datetime AS DATE)
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
                    CAST(start_datetime AS DATE) as date,
                        SUM(price_value) as revenue
                    FROM bookings.booking
                    WHERE club_id = :clubId
                    AND start_datetime < :end
                    AND end_datetime > :start
                    AND status = 'PAID'
                    GROUP BY CAST(start_datetime AS DATE)
                    ORDER BY CAST(start_datetime AS DATE)
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

    /**
     * Получение бронирований в клубе за диапазон дат
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT b.*
                    FROM bookings.booking b
                    WHERE club_id = :clubId
                    AND b.start_datetime < :end
                    AND b.end_datetime > :start
                    """
    )
    List<Booking> findAllByDateRangeAndClubId(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end
    );

    /**
     * Проверка существования бронирований в клубе за диапазон дат
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT (COALESCE(COUNT(*), 0) > 0) FROM bookings.booking
                    WHERE club_id = :clubId
                    AND start_datetime < :end
                    AND end_datetime > :start
                    """
    )
    boolean existsByDateRangeAndClubId(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end
    );

    /**
     * Получение бронирований в клубе за диапазон дат для клиента
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT b.*
                    FROM bookings.booking b
                    JOIN clients.client c ON b.client_id = c.id
                    WHERE b.start_datetime < :end
                    AND b.end_datetime > :start
                    AND c.id = :clientId
                    """
    )
    List<Booking> findAllByDateRangeAndClientId(
            @Param(value = "clientId") long clientId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end
    );

    /**
     * Получение бронирований в клубе за диапазон дат для клиента с пагинацией
     */
    @Query(
            value = """
                    SELECT b
                    FROM Booking b
                    WHERE b.client.id = :clientId
                    AND b.startDateTime < :end
                    AND b.endDateTime > :start
                    """
    )
    Page<Booking> findAllByDateRangeAndClientIdPaged(
            @Param(value = "clientId") long clientId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end,
            Pageable pageable
    );

    /**
     * Получение бронирований в клубе за диапазон дат, а также с указанным номером телефона клиента
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT b.*
                    FROM bookings.booking b
                    JOIN clients.client c ON b.client_id = c.id
                    WHERE b.club_id = :clubId
                    AND b.start_datetime < :end
                    AND b.end_datetime > :start
                    AND c.phone = :clientPhone
                    """
    )
    List<Booking> findAllByDateRangeAndClubIdAndClientPhone(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end,
            @Param(value = "clientPhone") String clientPhone
    );

    /**
     * Получение бронирований в клубе за диапазон дат с пагинацией
     */
    @Query(
            value = """
                    SELECT b
                    FROM Booking b
                    WHERE b.club.id = :clubId
                    AND b.startDateTime < :end
                    AND b.endDateTime > :start
                    """
    )
    Page<Booking> findAllByDateRangeAndClubIdPaged(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end,
            Pageable pageable
    );

    /**
     * Получение бронирований в клубе за диапазон дат с указанным номером телефона клиента с пагинацией
     */
    @Query(
            value = """
                    SELECT b
                    FROM Booking b
                    WHERE b.club.id = :clubId
                    AND b.startDateTime < :end
                    AND b.endDateTime > :start
                    AND b.client.phone = :clientPhone
                    """
    )
    Page<Booking> findAllByDateRangeAndClubIdAndClientPhonePaged(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") OffsetDateTime start,
            @Param(value = "end") OffsetDateTime end,
            @Param(value = "clientPhone") String clientPhone,
            Pageable pageable
    );

    Optional<Booking> findByIdAndClub(long bookingId, Club club);
}
