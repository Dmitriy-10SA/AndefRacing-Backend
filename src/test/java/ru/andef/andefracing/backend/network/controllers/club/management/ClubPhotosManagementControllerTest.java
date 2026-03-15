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
import ru.andef.andefracing.backend.network.dtos.management.AddPhotoDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClubPhotosManagementController.class)
class ClubPhotosManagementControllerTest {

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
        return new UsernamePasswordAuthenticationToken(principal, null, emptyList());
    }

    @Test
    void addPhotoInClubReturnsOkWhenValidAndAuthenticated() throws Exception {
        AddPhotoDto dto = new AddPhotoDto("http://example.com/photo.jpg", (short) 1);

        mockMvc.perform(post("/api/v1/management/club/photos")
                        .with(authentication(employeeAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePhotoFromClubReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/v1/management/club/photos/1")
                        .with(authentication(employeeAuth())))
                .andExpect(status().isOk());
    }

    @Test
    void reorderPhotosInClubReturnsOkWhenValidAndAuthenticated() throws Exception {
        List<Long> ids = List.of(1L, 2L, 3L);

        mockMvc.perform(patch("/api/v1/management/club/photos/reorder")
                        .with(authentication(employeeAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk());
    }
}

