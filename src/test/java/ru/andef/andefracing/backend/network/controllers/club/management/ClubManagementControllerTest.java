package ru.andef.andefracing.backend.network.controllers.club.management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClubManagementController.class)
class ClubManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private ClubManagementService clubManagementService;

    private Authentication employeeAuth(long employeeId, int clubId) {
        JwtFilter.EmployeePrincipal principal = new JwtFilter.EmployeePrincipal(employeeId, clubId, "Club");
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void updateCntEquipmentInClubReturnsOkWhenValidAndAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/v1/management/club")
                        .with(authentication(employeeAuth(1L, 2)))
                        .param("cntEquipment", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void openClubReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/v1/management/club/open")
                        .with(authentication(employeeAuth(1L, 2))))
                .andExpect(status().isOk());
    }

    @Test
    void closeClubReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/v1/management/club/close")
                        .with(authentication(employeeAuth(1L, 2))))
                .andExpect(status().isOk());
    }
}

