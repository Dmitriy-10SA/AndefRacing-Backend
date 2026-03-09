package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/club-management")
@Validated
public class ClubManagementController {
    /**
     * Изменение количества симуляторов в выбранном текущим клубе
     */
    @PatchMapping("/{clubId}")
    public ResponseEntity<Void> updateCntEquipmentInClub(
            @PathVariable int clubId,
            @RequestParam(name = "cntEquipment") @Min(1) int cntEquipment
    ) {
        // TODO: Проверить, что count >= 0, и обновить количество симуляторов
        return ResponseEntity.ok().build();
    }

    /**
     * Открыть клуб
     */
    @PatchMapping("/{clubId}/open")
    public ResponseEntity<Void> openClub(@PathVariable int clubId) {
        // TODO: Установить статус клуба как "открыт"
        return ResponseEntity.ok().build();
    }

    /**
     * Закрыть клуб
     */
    @PatchMapping("/{clubId}/close")
    public ResponseEntity<Void> closeClub(@PathVariable int clubId) {
        // TODO: Установить статус клуба как "закрыт"
        return ResponseEntity.ok().build();
    }
}