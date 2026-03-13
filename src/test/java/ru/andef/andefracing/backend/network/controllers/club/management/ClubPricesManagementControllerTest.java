package ru.andef.andefracing.backend.network.controllers.club.management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClubPricesManagementController.class)
class ClubPricesManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClubManagementService clubManagementService;

    private Authentication employeeAuth() {
        JwtFilter.EmployeePrincipal principal = new JwtFilter.EmployeePrincipal(1L, 2, "Club");
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void addPriceForMinutesInClubReturnsOkWhenValidAndAuthenticated() throws Exception {
        AddPriceDto dto = new AddPriceDto((short) 60, BigDecimal.TEN);

        mockMvc.perform(post("/api/v1/management/club/prices")
                        .with(authentication(employeeAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePriceForMinutesInClubReturnsOkWhenValidAndAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/v1/management/club/prices/1")
                        .with(authentication(employeeAuth()))
                        .param("value", "100"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePriceForMinutesInClubReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/v1/management/club/prices/1")
                        .with(authentication(employeeAuth())))
                .andExpect(status().isOk());
    }
}

