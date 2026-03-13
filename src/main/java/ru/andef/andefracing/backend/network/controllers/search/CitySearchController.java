package ru.andef.andefracing.backend.network.controllers.search;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.domain.services.search.LocationSearchService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;

import java.util.List;

@Tag(name = "Search")
@RestController
@RequestMapping(ApiPaths.CITIES_SEARCH)
@RequiredArgsConstructor
public class CitySearchController {
    private final LocationSearchService locationSearchService;

    /**
     * Получение всех городов в указанном регионе, где есть открытые клубы
     */
    @GetMapping(path = "/{regionId}", version = ApiVersions.V1)
    public ResponseEntity<List<CityShortDto>> getAllCitiesInRegionWithOpenClubs(@PathVariable short regionId) {
        List<CityShortDto> cities = locationSearchService.getAllCitiesInRegionWithOpenClubs(regionId);
        return ResponseEntity.ok(cities);
    }
}