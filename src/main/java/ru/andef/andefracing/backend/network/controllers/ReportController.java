package ru.andef.andefracing.backend.network.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.domain.services.ReportService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiTags;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.report.BookingStatisticsDto;
import ru.andef.andefracing.backend.network.dtos.report.FinancialStatisticsDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.time.LocalDate;

@Tag(name = ApiTags.REPORT)
@RestController
@RequestMapping(ApiPaths.REPORTS)
@Validated
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    /**
     * Получение отчета «Cтатистика бронирований», который включает:
     * общее число бронирований, процент отмен, число бронирований по дням
     */
    @GetMapping(path = "/booking-statistics", version = ApiVersions.V1)
    public ResponseEntity<BookingStatisticsDto> getBookingStatistics(
            @RequestParam("startDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        BookingStatisticsDto bookingStatisticsDto = reportService
                .getBookingStatistics(principal.clubId(), startDate, endDate);
        return ResponseEntity.ok(bookingStatisticsDto);
    }

    /**
     * Получение отчета «Финансовая статистика», который включает:
     * общую выручку, выручку по дням, средний чек
     */
    @GetMapping(path = "/financial-statistics", version = ApiVersions.V1)
    public ResponseEntity<FinancialStatisticsDto> getFinancialStatistics(
            @RequestParam("startDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        FinancialStatisticsDto financialStatistics = reportService
                .getFinancialStatistics(principal.clubId(), startDate, endDate);
        return ResponseEntity.ok(financialStatistics);
    }
}