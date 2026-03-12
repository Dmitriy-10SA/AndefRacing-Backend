package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.ManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;
import ru.andef.andefracing.backend.network.security.JwtFilter;

import java.math.BigDecimal;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_PRICES)
@Validated
@RequiredArgsConstructor
public class ClubPricesManagementController {
    private final ManagementService managementService;

    /**
     * Добавление цены за кол-во минут игры в клубе
     */
    @PostMapping
    public ResponseEntity<Void> addPriceForMinutesInClub(
            @RequestBody @Valid AddPriceDto addPriceDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.addPriceForMinutesInClub(principal.clubId(), addPriceDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение цены за кол-во минут игры в клубе
     */
    @PatchMapping("/{priceId}")
    public ResponseEntity<Void> updatePriceForMinutesInClub(
            @PathVariable long priceId,
            @RequestParam(name = "value") @NotNull @Min(1) BigDecimal value,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.updatePriceForMinutesInClub(principal.clubId(), priceId, value);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление цены за кол-во минут игры в клубе
     */
    @DeleteMapping("/{priceId}")
    public ResponseEntity<Void> deletePriceForMinutesInClub(
            @PathVariable long priceId,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.deletePriceForMinutesInClub(principal.clubId(), priceId);
        return ResponseEntity.ok().build();
    }
}