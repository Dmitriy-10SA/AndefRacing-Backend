package ru.andef.andefracing.backend.network.controllers.management;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.ManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;
import ru.andef.andefracing.backend.network.security.JwtFilter;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_GAMES)
@RequiredArgsConstructor
public class ClubGamesManagementController {
    private final ManagementService managementService;

    /**
     * Добавить активную игру в клуб (из справочника)
     */
    @PostMapping("{gameId}")
    public ResponseEntity<Void> addGameToClub(@PathVariable short gameId, Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.addGameToClub(principal.clubId(), gameId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение справочника игр (только активных)
     */
    @GetMapping
    public ResponseEntity<List<GameDto>> getAllActiveGames() {
        List<GameDto> games = managementService.getAllActiveGames();
        return ResponseEntity.ok(games);
    }

    /**
     * Удалить игру из клуба
     */
    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> removeGameFromClub(@PathVariable short gameId, Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.deleteGameInClub(principal.clubId(), gameId);
        return ResponseEntity.ok().build();
    }
}