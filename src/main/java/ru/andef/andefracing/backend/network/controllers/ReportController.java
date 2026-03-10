package ru.andef.andefracing.backend.network.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.report.BookingStatisticsDto;
import ru.andef.andefracing.backend.network.dtos.report.FinancialStatisticsDto;

import java.time.LocalDate;

@RestController
@RequestMapping(ApiPaths.REPORTS)
@Validated
public class ReportController {
    /**
     * Получение отчета «Cтатистика бронирований», который включает:
     * общее число бронирований, процент отмен, число бронирований по дням
     */
    @GetMapping("/booking-statistics")
    public ResponseEntity<BookingStatisticsDto> getBookingStatistics(
            @RequestParam("startDate") @NotNull LocalDate startDate,
            @RequestParam("endDate") @NotNull LocalDate endDate
    ) {
        // TODO
        return null;
    }

    /**
     * Получение отчета «Финансовая статистика», который включает:
     * общую выручку, выручку по дням, средний чек
     */
    @GetMapping("/financial-statistics")
    public ResponseEntity<FinancialStatisticsDto> getFinancialStatistics(
            @RequestParam("startDate") @NotNull LocalDate startDate,
            @RequestParam("endDate") @NotNull LocalDate endDate
    ) {
        // TODO
        return null;
    }
}