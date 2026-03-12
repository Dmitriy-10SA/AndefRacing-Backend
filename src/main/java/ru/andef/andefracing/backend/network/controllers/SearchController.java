package ru.andef.andefracing.backend.network.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.SearchService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SEARCH)
@Validated
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    /**
     * Получение всех регионов
     */
    @GetMapping("/regions")
    public ResponseEntity<List<RegionShortDto>> getAllRegions() {
        List<RegionShortDto> regions = searchService.getAllRegions();
        return ResponseEntity.ok(regions);
    }

    /**
     * Получение всех городов в указанном регионе
     */
    @GetMapping("/cities/{regionId}")
    public ResponseEntity<List<CityShortDto>> getAllCitiesInRegion(@PathVariable short regionId) {
        List<CityShortDto> cities = searchService.getAllCitiesInRegion(regionId);
        return ResponseEntity.ok(cities);
    }

    /**
     * Получение всех клубов (работающих) в указанном городе с пагинацией
     */
    @GetMapping("/clubs/{cityId}")
    public ResponseEntity<PagedClubShortListDto> getAllClubsInCity(
            @PathVariable short cityId,
            @RequestParam @Min(value = 0) int pageNumber,
            @RequestParam @Min(value = 1) @Max(value = 100) int pageSize
    ) {
        PagedClubShortListDto pagedClubShortListDto = searchService.getAllClubsInCity(cityId, pageNumber, pageSize);
        return ResponseEntity.ok(pagedClubShortListDto);
    }

    /**
     * Получение подробной информации о клубе
     */
    @GetMapping("/club-full-info/{clubId}")
    public ResponseEntity<ClubFullInfoDto> getClubFullInfo(@PathVariable int clubId) {
        ClubFullInfoDto clubFullInfoDto = searchService.getClubFullInfo(clubId);
        return ResponseEntity.ok(clubFullInfoDto);
    }
}