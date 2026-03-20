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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClubGamesManagementController.class)
class ClubGamesManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private ClubManagementService clubManagementService;

    private Authentication employeeAuth() {
        JwtFilter.EmployeePrincipal principal = new JwtFilter.EmployeePrincipal(1L, 2, "Club");
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void addGameToClubReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/management/club/games/1")
                        .with(authentication(employeeAuth())))
                .andExpect(status().isOk());
    }

    @Test
    void getAllActiveGamesReturnsOk() throws Exception {
        when(clubManagementService.getAllActiveGames(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/management/club/games"))
                .andExpect(status().isOk());
    }

    @Test
    void removeGameFromClubReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/v1/management/club/games/1")
                        .with(authentication(employeeAuth())))
                .andExpect(status().isOk());
    }
}

