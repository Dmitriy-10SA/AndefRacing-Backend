package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.domain.services.ManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.security.JwtFilter;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT)
@Validated
@RequiredArgsConstructor
public class ClubManagementController {
    private final ManagementService managementService;

    /**
     * Изменение количества симуляторов в выбранном текущим клубе
     */
    @PatchMapping
    public ResponseEntity<Void> updateCntEquipmentInClub(
            @RequestParam(name = "cntEquipment") @Min(1) @Max(10000) short cntEquipment,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.updateCntEquipmentInClub(principal.clubId(), cntEquipment);
        return ResponseEntity.ok().build();
    }

    /**
     * Открыть клуб
     */
    @PatchMapping("/open")
    public ResponseEntity<Void> openClub(Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.openClub(principal.clubId());
        return ResponseEntity.ok().build();
    }

    /**
     * Закрыть клуб
     */
    @PatchMapping("/close")
    public ResponseEntity<Void> closeClub(Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.closeClub(principal.clubId());
        return ResponseEntity.ok().build();
    }
}