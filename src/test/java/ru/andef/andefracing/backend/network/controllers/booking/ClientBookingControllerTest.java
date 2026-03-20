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
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientMakeBookingDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.PagedClientBookingShortListDto;
import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientBookingController.class)
class ClientBookingControllerTest {

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

    private Authentication clientAuth() {
        JwtFilter.ClientPrincipal principal = new JwtFilter.ClientPrincipal(1L);
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void getFreeBookingSlotsInClubReturnsOkWhenValid() throws Exception {
        FreeBookingSlotsRequestDto dto = new FreeBookingSlotsRequestDto(
                (short) 60,
                (short) 1,
                LocalDate.of(2026, 1, 1)
        );

        mockMvc.perform(get("/api/v1/bookings/client/free-slots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void makeBookingReturnsOkWhenValidAndAuthenticated() throws Exception {
        ClientMakeBookingDto dto = new ClientMakeBookingDto(
                (short) 1,
                new ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto(
                        LocalDateTime.of(2026, 1, 1, 10, 0),
                        LocalDateTime.of(2026, 1, 1, 11, 0)
                ),
                "note"
        );

        mockMvc.perform(post("/api/v1/bookings/client/make-booking/1")
                        .with(authentication(clientAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsReturnsOkWhenValidAndAuthenticated() throws Exception {
        PagedClientBookingShortListDto pagedDto = new PagedClientBookingShortListDto(
                Collections.emptyList(),
                new PageInfoDto(0, 10, 0L, 0, true)
        );
        when(bookingSearchService.getAllClientBookingsPaged(
                anyLong(), any(LocalDate.class), any(LocalDate.class), anyInt(), anyInt()
        )).thenReturn(pagedDto);

        mockMvc.perform(get("/api/v1/bookings/client")
                        .with(authentication(clientAuth()))
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getFullBookingInfoReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/client/1/1")
                        .with(authentication(clientAuth())))
                .andExpect(status().isOk());
    }
}

