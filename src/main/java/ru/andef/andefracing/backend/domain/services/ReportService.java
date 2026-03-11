package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.projections.BookingStatsAggregateProjection;
import ru.andef.andefracing.backend.data.projections.BookingsPerDayProjection;
import ru.andef.andefracing.backend.data.projections.FinancialStatsAggregateProjection;
import ru.andef.andefracing.backend.data.projections.RevenuePerDayProjection;
import ru.andef.andefracing.backend.data.repositories.club.BookingRepository;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.network.dtos.report.BookingStatisticsDto;
import ru.andef.andefracing.backend.network.dtos.report.FinancialStatisticsDto;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final BookingRepository bookingRepository;
    private final ClubRepository clubRepository;

    /**
     * Получение клуба по id или выброс исключения
     */
    private Club findClubByIdOrThrow(int clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Клуб с id " + clubId + " не найден"));
    }

    /**
     * Получение отчета «Cтатистика бронирований», который включает:
     * общее число бронирований, процент отмен, число бронирований по дням
     */
    @Transactional(readOnly = true)
    public BookingStatisticsDto getBookingStatistics(int clubId, LocalDate startDate, LocalDate endDate) {
        Club club = findClubByIdOrThrow(clubId);
        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        BookingStatsAggregateProjection bookingStatsAggregateProjection = bookingRepository
                .getBookingStatsAggregate(club.getId(), start, end);
        List<BookingsPerDayProjection> bookingsPerDayProjection = bookingRepository
                .getBookingsPerDay(club.getId(), start, end);
        List<BookingStatisticsDto.DateAndBookingsCountDto> dateAndBookingsCount = bookingsPerDayProjection.stream()
                .map(it ->
                        new BookingStatisticsDto.DateAndBookingsCountDto(it.getDate(), it.getBookingsCount())
                )
                .toList();
        return new BookingStatisticsDto(
                club.getId(),
                startDate,
                endDate,
                bookingStatsAggregateProjection.getBookingsCount(),
                bookingStatsAggregateProjection.getCancellationsPercent(),
                dateAndBookingsCount
        );
    }

    /**
     * Получение отчета «Финансовая статистика», который включает:
     * общую выручку, выручку по дням, средний чек
     */
    @Transactional(readOnly = true)
    public FinancialStatisticsDto getFinancialStatistics(int clubId, LocalDate startDate, LocalDate endDate) {
        Club club = findClubByIdOrThrow(clubId);
        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        FinancialStatsAggregateProjection financialStatsAggregateProjection = bookingRepository
                .getFinancialStatAggregate(club.getId(), start, end);
        List<RevenuePerDayProjection> revenuePerDayProjection = bookingRepository
                .getRevenuePerDay(club.getId(), start, end);
        List<FinancialStatisticsDto.DateAndTotalRevenueDto> dateAndTotalRevenues = revenuePerDayProjection.stream()
                .map(it ->
                        new FinancialStatisticsDto.DateAndTotalRevenueDto(
                                it.getDate(),
                                it.getRevenue().setScale(2, RoundingMode.HALF_EVEN)
                        )
                )
                .toList();
        return new FinancialStatisticsDto(
                club.getId(),
                startDate,
                endDate,
                financialStatsAggregateProjection.getTotalRevenue().setScale(2, RoundingMode.HALF_EVEN),
                dateAndTotalRevenues,
                financialStatsAggregateProjection.getAverageReceipt().setScale(2, RoundingMode.HALF_EVEN)
        );
    }
}
