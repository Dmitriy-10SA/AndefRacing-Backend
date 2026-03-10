package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.network.ApiPaths;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT)
@Validated
public class ClubManagementController {
    /**
     * Изменение количества симуляторов в выбранном текущим клубе
     */
    @PatchMapping
    public ResponseEntity<Void> updateCntEquipmentInClub(
            @RequestParam(name = "cntEquipment") @Min(1) int cntEquipment
    ) {
        // TODO: Проверить, что count >= 0, и обновить количество симуляторов
        return ResponseEntity.ok().build();
    }

    /**
     * Открыть клуб
     */
    @PatchMapping("/open")
    public ResponseEntity<Void> openClub() {
        // TODO: Установить статус клуба как "открыт"
        return ResponseEntity.ok().build();
    }

    /**
     * Закрыть клуб
     */
    @PatchMapping("/close")
    public ResponseEntity<Void> closeClub() {
        // TODO: Установить статус клуба как "закрыт"
        return ResponseEntity.ok().build();
    }
}