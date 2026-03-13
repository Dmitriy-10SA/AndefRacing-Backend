package ru.andef.andefracing.backend.network.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.SearchService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

import java.util.List;

@Tag(name = "Search")
@RestController
@RequestMapping(ApiPaths.SEARCH)
@Validated
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    /**
     * Получение всех регионов, где есть открытые клубы
     */
    @GetMapping(path = "/regions", version = ApiVersions.V1)
    public ResponseEntity<List<RegionShortDto>> getAllRegionsWithOpenClubs() {
        List<RegionShortDto> regions = searchService.getAllRegionsWithOpenClubs();
        return ResponseEntity.ok(regions);
    }

    /**
     * Получение всех городов в указанном регионе, где есть открытые клубы
     */
    @GetMapping(path = "/cities/{regionId}", version = ApiVersions.V1)
    public ResponseEntity<List<CityShortDto>> getAllCitiesInRegionWithOpenClubs(@PathVariable short regionId) {
        List<CityShortDto> cities = searchService.getAllCitiesInRegionWithOpenClubs(regionId);
        return ResponseEntity.ok(cities);
    }

    /**
     * Получение всех клубов (работающих) в указанном городе с пагинацией
     */
    @GetMapping(path = "/clubs/{cityId}", version = ApiVersions.V1)
    public ResponseEntity<PagedClubShortListDto> getAllOpenClubsInCity(
            @PathVariable short cityId,
            @RequestParam @Min(value = 0) int pageNumber,
            @RequestParam @Min(value = 1) @Max(value = 100) int pageSize
    ) {
        PagedClubShortListDto pagedClubShortListDto = searchService.getAllOpenClubsInCity(cityId, pageNumber, pageSize);
        return ResponseEntity.ok(pagedClubShortListDto);
    }

    /**
     * Получение подробной информации о клубе
     */
    @GetMapping(path = "/club-full-info/{clubId}", version = ApiVersions.V1)
    public ResponseEntity<ClubFullInfoDto> getClubFullInfo(@PathVariable int clubId) {
        ClubFullInfoDto clubFullInfoDto = searchService.getClubFullInfo(clubId);
        return ResponseEntity.ok(clubFullInfoDto);
    }
}