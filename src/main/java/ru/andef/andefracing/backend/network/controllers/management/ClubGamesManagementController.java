package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;

@RestController
@RequestMapping("/club-management/games")
public class ClubGamesManagementController {
    /**
     * Добавить активную игру в клуб (из справочника)
     */
    @PostMapping("/{clubId}")
    public ResponseEntity<Void> addGameToClub(
            @PathVariable int clubId,
            @RequestBody @Valid GameDto gameDto
    ) {
        // TODO: Добавить игру в список активных игр клуба
        return ResponseEntity.ok().build();
    }

    /**
     * Удалить игру из клуба
     */
    @DeleteMapping("/{clubId}/{gameId}")
    public ResponseEntity<Void> removeGameFromClub(@PathVariable int clubId, @PathVariable short gameId) {
        // TODO: Удалить игру из списка активных игр клуба
        return ResponseEntity.ok().build();
    }
}
