package ru.andef.andefracing.backend.network.controllers.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.search.LocationSearchService;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegionSearchController.class)
class RegionSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private LocationSearchService locationSearchService;

    @Test
    void getAllRegionsWithOpenClubsReturnsOk() throws Exception {
        when(locationSearchService.getAllRegionsWithOpenClubs()).thenReturn(List.of(
                new RegionShortDto((short) 1, "Region 1")
        ));

        mockMvc.perform(get("/api/v1/search/regions"))
                .andExpect(status().isOk());
    }
}

