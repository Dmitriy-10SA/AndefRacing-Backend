package ru.andef.andefracing.backend.network.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.ReportService;
import ru.andef.andefracing.backend.network.dtos.report.BookingStatisticsDto;
import ru.andef.andefracing.backend.network.dtos.report.FinancialStatisticsDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private Authentication employeeAuth(long employeeId, int clubId) {
        JwtFilter.EmployeePrincipal principal = new JwtFilter.EmployeePrincipal(employeeId, clubId, "Club");
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void getBookingStatisticsReturnsOkWhenAuthenticatedAndValidDates() throws Exception {
        BookingStatisticsDto dto = new BookingStatisticsDto(
                2,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                0L,
                BigDecimal.ZERO,
                Collections.emptyList()
        );
        when(reportService.getBookingStatistics(anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/reports/booking-statistics")
                        .with(authentication(employeeAuth(1L, 2)))
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    void getFinancialStatisticsReturnsOkWhenAuthenticatedAndValidDates() throws Exception {
        FinancialStatisticsDto dto = new FinancialStatisticsDto(
                2,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                BigDecimal.ZERO,
                Collections.emptyList(),
                BigDecimal.ZERO
        );
        when(reportService.getFinancialStatistics(anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/reports/financial-statistics")
                        .with(authentication(employeeAuth(1L, 2)))
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk());
    }
}

