package ru.andef.andefracing.backend.network.controllers.club.management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.UpdateWorkScheduleDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import tools.jackson.databind.ObjectMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClubWorkScheduleManagementController.class)
class ClubWorkScheduleManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private ClubManagementService clubManagementService;

    private Authentication employeeAuth() {
        JwtFilter.EmployeePrincipal principal = new JwtFilter.EmployeePrincipal(1L, 2, "Club");
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void addWorkScheduleExceptionInClubReturnsOkWhenValidAndAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/management/club/work-schedule/exceptions")
                        .with(authentication(employeeAuth())))
                .andExpect(status().isOk());
    }

    @Test
    void getAllWorkSchedulesExceptionsInClubReturnsOkWhenAuthenticated() throws Exception {
        when(clubManagementService.getAllWorkSchedulesExceptionsInClub(anyLong(), anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/management/club/work-schedule/exceptions")
                        .with(authentication(employeeAuth()))
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteWorkScheduleExceptionInClubReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/v1/management/club/work-schedule/exceptions/1")
                        .with(authentication(employeeAuth())))
                .andExpect(status().isOk());
    }

    @Test
    void updateWorkScheduleInClubReturnsOkWhenValidAndAuthenticated() throws Exception {
        UpdateWorkScheduleDto dto = new UpdateWorkScheduleDto(
                DayOfWeek.FRIDAY,
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                true
        );

        mockMvc.perform(put("/api/v1/management/club/work-schedule")
                        .with(authentication(employeeAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}

