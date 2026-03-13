package ru.andef.andefracing.backend.network.controllers.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import tools.jackson.databind.ObjectMapper;
import ru.andef.andefracing.backend.domain.services.search.ClubSearchService;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;
import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClubSearchController.class)
class ClubSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClubSearchService clubSearchService;

    @Test
    void getAllOpenClubsInCityReturnsOkWhenValidParams() throws Exception {
        PagedClubShortListDto paged = new PagedClubShortListDto(
                Collections.emptyList(),
                new PageInfoDto(0, 10, 0L, 0, true)
        );
        when(clubSearchService.getAllOpenClubsInCity(anyShort(), anyInt(), anyInt())).thenReturn(paged);

        mockMvc.perform(get("/api/v1/search/clubs/1")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getClubFullInfoReturnsOk() throws Exception {
        when(clubSearchService.getClubFullInfo(anyInt())).thenReturn(
                new ClubFullInfoDto(
                        1,
                        "Club",
                        "+7-999-999-99-99",
                        "club@mail.com",
                        "Address",
                        (short) 10,
                        true,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );

        mockMvc.perform(get("/api/v1/search/clubs/1/full-info"))
                .andExpect(status().isOk());
    }
}

