package ru.andef.andefracing.backend.network.controllers.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.ProfileService;
import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientChangePersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.PagedFavoriteClubShortListDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientProfileController.class)
class ClientProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private ProfileService profileService;

    private Authentication clientAuth() {
        JwtFilter.ClientPrincipal principal = new JwtFilter.ClientPrincipal(1L);
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void getPersonalInfoReturnsOkWhenAuthenticated() throws Exception {
        when(profileService.getClientPersonalInfo(1L)).thenReturn(
                new ClientPersonalInfoDto("Name", "+7-999-999-99-99")
        );

        mockMvc.perform(get("/api/v1/profile/client/personal-info")
                        .with(authentication(clientAuth())))
                .andExpect(status().isOk());
    }

    @Test
    void changePersonalInfoReturnsOkWhenValidDtoAndAuthenticated() throws Exception {
        ClientChangePersonalInfoDto dto = new ClientChangePersonalInfoDto("Name", "+7-999-999-99-99");

        mockMvc.perform(patch("/api/v1/profile/client/change-personal-info")
                        .with(authentication(clientAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void addFavoriteClubReturnsOkWhenAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/profile/client/favorite-clubs/1")
                        .with(authentication(clientAuth())))
                .andExpect(status().isOk());
    }

    @Test
    void getFavoriteClubsReturnsOkWhenValidParamsAndAuthenticated() throws Exception {
        when(profileService.getClientFavoriteClubs(1L, 0, 10)).thenReturn(
                new PagedFavoriteClubShortListDto(
                        Collections.emptyList(),
                        new PageInfoDto(0, 10, 0L, 0, true)
                )
        );

        mockMvc.perform(get("/api/v1/profile/client/favorite-clubs")
                        .with(authentication(clientAuth()))
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }
}

