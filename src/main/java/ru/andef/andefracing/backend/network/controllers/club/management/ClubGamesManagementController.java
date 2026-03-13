package ru.andef.andefracing.backend.network.controllers.club.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.util.List;

@Tag(name = "Management - игры")
@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_GAMES)
@RequiredArgsConstructor
public class ClubGamesManagementController {
    private final ClubManagementService clubManagementService;

    /**
     * Добавить активную игру в клуб (из справочника)
     */
    @PostMapping(path = "{gameId}", version = ApiVersions.V1)
    public ResponseEntity<Void> addGameToClub(@PathVariable short gameId, Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.addGameToClub(principal.clubId(), gameId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение справочника игр (только активных)
     */
    @GetMapping(version = ApiVersions.V1)
    public ResponseEntity<List<GameDto>> getAllActiveGames() {
        List<GameDto> games = clubManagementService.getAllActiveGames();
        return ResponseEntity.ok(games);
    }

    /**
     * Удалить игру из клуба
     */
    @DeleteMapping(path = "/{gameId}", version = ApiVersions.V1)
    public ResponseEntity<Void> removeGameFromClub(@PathVariable short gameId, Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.deleteGameInClub(principal.clubId(), gameId);
        return ResponseEntity.ok().build();
    }
}