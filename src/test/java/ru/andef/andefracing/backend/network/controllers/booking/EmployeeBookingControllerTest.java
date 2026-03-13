package ru.andef.andefracing.backend.network.controllers.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.booking.BookingManagementService;
import ru.andef.andefracing.backend.domain.services.booking.BookingSearchService;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotsRequestDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeMakeBookingDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeBookingController.class)
class EmployeeBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingSearchService bookingSearchService;

    @MockitoBean
    private BookingManagementService bookingManagementService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private Authentication employeeAuth(long employeeId, int clubId) {
        JwtFilter.EmployeePrincipal principal = new JwtFilter.EmployeePrincipal(employeeId, clubId, "Club");
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void getFreeBookingSlotsInClubReturnsOkWhenValidAndAuthenticated() throws Exception {
        FreeBookingSlotsRequestDto dto = new FreeBookingSlotsRequestDto(
                (short) 60,
                (short) 1,
                LocalDate.of(2026, 1, 1)
        );

        mockMvc.perform(get("/api/v1/bookings/employee/free-slots")
                        .with(authentication(employeeAuth(1L, 2)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void confirmBookingPaymentReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/v1/bookings/employee/confirm-booking-payment/1")
                        .with(authentication(employeeAuth(1L, 2))))
                .andExpect(status().isOk());
    }

    @Test
    void makeBookingReturnsOkWhenValidAndAuthenticated() throws Exception {
        EmployeeMakeBookingDto dto = new EmployeeMakeBookingDto(
                (short) 1,
                java.math.BigDecimal.TEN,
                new ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto(
                        OffsetDateTime.of(
                                LocalDateTime.of(2026, 1, 1, 10, 0),
                                ZoneOffset.UTC
                        ),
                        OffsetDateTime.of(
                                LocalDateTime.of(2026, 1, 1, 11, 0),
                                ZoneOffset.UTC
                        )
                ),
                "note"
        );

        mockMvc.perform(post("/api/v1/bookings/employee/make-booking")
                        .with(authentication(employeeAuth(1L, 2)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void cancelBookingReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/v1/bookings/employee/cancel/1")
                        .with(authentication(employeeAuth(1L, 2))))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsReturnsOkWhenValidAndAuthenticated() throws Exception {
        when(bookingSearchService.getBookingsForEmployee(anyLong(), anyInt(), any(LocalDate.class), any(LocalDate.class), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/bookings/employee")
                        .with(authentication(employeeAuth(1L, 2)))
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    void getFullBookingInfoReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/employee/1")
                        .with(authentication(employeeAuth(1L, 2))))
                .andExpect(status().isOk());
    }
}

