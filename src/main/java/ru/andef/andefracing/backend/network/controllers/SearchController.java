package ru.andef.andefracing.backend.network.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SEARCH)
public class SearchController {
    /**
     * Получение всех регионов
     */
    @GetMapping("/regions")
    public ResponseEntity<List<RegionShortDto>> getAllRegions() {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение всех городов в указанном регионе
     */
    @GetMapping("/cities/{regionId}")
    public ResponseEntity<List<CityShortDto>> getAllCitiesInRegion(@PathVariable short regionId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение всех клубов (работающих) в указанном городе с пагинацией
     */
    @GetMapping("/clubs/{cityId}")
    public ResponseEntity<PagedClubShortListDto> getAllClubsInCity(@PathVariable short cityId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение подробной информации о клубе
     */
    @GetMapping("/club-full-info/{clubId}")
    public ResponseEntity<ClubFullInfoDto> getClubFullInfo(@PathVariable int clubId) {
        // TODO
        return ResponseEntity.ok(null);
    }
}