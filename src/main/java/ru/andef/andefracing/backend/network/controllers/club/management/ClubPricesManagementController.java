package ru.andef.andefracing.backend.network.controllers.club.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.math.BigDecimal;

@Tag(name = "Management - цены")
@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_PRICES)
@Validated
@RequiredArgsConstructor
public class ClubPricesManagementController {
    private final ClubManagementService clubManagementService;

    /**
     * Добавление цены за кол-во минут игры в клубе
     */
    @PostMapping(version = ApiVersions.V1)
    public ResponseEntity<Void> addPriceForMinutesInClub(
            @RequestBody @Valid AddPriceDto addPriceDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.addPriceForMinutesInClub(principal.clubId(), addPriceDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение цены за кол-во минут игры в клубе
     */
    @PatchMapping(path = "/{priceId}", version = ApiVersions.V1)
    public ResponseEntity<Void> updatePriceForMinutesInClub(
            @PathVariable long priceId,
            @RequestParam(name = "value") @NotNull @Min(1) BigDecimal value,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.updatePriceForMinutesInClub(principal.clubId(), priceId, value);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление цены за кол-во минут игры в клубе
     */
    @DeleteMapping(path = "/{priceId}", version = ApiVersions.V1)
    public ResponseEntity<Void> deletePriceForMinutesInClub(
            @PathVariable long priceId,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.deletePriceForMinutesInClub(principal.clubId(), priceId);
        return ResponseEntity.ok().build();
    }
}