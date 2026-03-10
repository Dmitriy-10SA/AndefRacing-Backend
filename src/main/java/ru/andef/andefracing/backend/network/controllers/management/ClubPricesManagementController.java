package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;

import java.math.BigDecimal;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_PRICES)
@Validated
public class ClubPricesManagementController {
    /**
     * Добавление цены за кол-во минут игры
     */
    @PostMapping
    public ResponseEntity<Void> addPriceForMinutes(
            @PathVariable int clubId,
            @RequestBody @Valid AddPriceDto addPriceDto
    ) {
        // TODO: Добавить цену за указанные минуты
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение цены за кол-во минут игры
     */
    @PatchMapping("/{priceId}")
    public ResponseEntity<Void> updatePriceForMinutes(
            @PathVariable int clubId,
            @PathVariable long priceId,
            @RequestParam(name = "value") @NotNull @Min(1) BigDecimal value
    ) {
        // TODO: Обновить существующую цену за минуты
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление цены за кол-во минут игры
     */
    @DeleteMapping("/{priceId}")
    public ResponseEntity<Void> deletePriceForMinutes(@PathVariable int clubId, @PathVariable long priceId) {
        // TODO: Удалить цену за указанное количество минут
        return ResponseEntity.ok().build();
    }
}