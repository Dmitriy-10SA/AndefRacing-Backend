package ru.andef.andefracing.backend.network.controllers.search;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.domain.services.search.LocationSearchService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;

import java.util.List;

@Tag(name = "Search")
@RestController
@RequestMapping(ApiPaths.REGIONS_SEARCH)
@RequiredArgsConstructor
public class RegionSearchController {
    private final LocationSearchService locationSearchService;

    /**
     * Получение всех регионов, где есть открытые клубы
     */
    @GetMapping(version = ApiVersions.V1)
    public ResponseEntity<List<RegionShortDto>> getAllRegionsWithOpenClubs() {
        List<RegionShortDto> regions = locationSearchService.getAllRegionsWithOpenClubs();
        return ResponseEntity.ok(regions);
    }
}