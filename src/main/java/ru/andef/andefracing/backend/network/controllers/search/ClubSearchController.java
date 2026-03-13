package ru.andef.andefracing.backend.network.controllers.search;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.search.ClubSearchService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiTags;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

@Tag(name = ApiTags.SEARCH)
@RestController
@RequestMapping(ApiPaths.CLUBS_SEARCH)
@Validated
@RequiredArgsConstructor
public class ClubSearchController {
    private final ClubSearchService clubSearchService;

    /**
     * Получение всех клубов (работающих) в указанном городе с пагинацией
     */
    @GetMapping(path = "/{cityId}", version = ApiVersions.V1)
    public ResponseEntity<PagedClubShortListDto> getAllOpenClubsInCity(
            @PathVariable short cityId,
            @RequestParam @Min(value = 0) int pageNumber,
            @RequestParam @Min(value = 1) @Max(value = 100) int pageSize
    ) {
        PagedClubShortListDto pagedClubShortListDto = clubSearchService
                .getAllOpenClubsInCity(cityId, pageNumber, pageSize);
        return ResponseEntity.ok(pagedClubShortListDto);
    }

    /**
     * Получение подробной информации о клубе
     */
    @GetMapping(path = "/{clubId}/full-info", version = ApiVersions.V1)
    public ResponseEntity<ClubFullInfoDto> getClubFullInfo(@PathVariable int clubId) {
        ClubFullInfoDto clubFullInfoDto = clubSearchService.getClubFullInfo(clubId);
        return ResponseEntity.ok(clubFullInfoDto);
    }
}