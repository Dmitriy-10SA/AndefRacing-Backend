package ru.andef.andefracing.backend.network.controllers.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.search.LocationSearchService;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CitySearchController.class)
class CitySearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private LocationSearchService locationSearchService;

    @Test
    void getAllCitiesInRegionWithOpenClubsReturnsOk() throws Exception {
        when(locationSearchService.getAllCitiesInRegionWithOpenClubs(anyShort())).thenReturn(List.of(
                new CityShortDto((short) 1, "City 1")
        ));

        mockMvc.perform(get("/api/v1/search/cities/1"))
                .andExpect(status().isOk());
    }
}

