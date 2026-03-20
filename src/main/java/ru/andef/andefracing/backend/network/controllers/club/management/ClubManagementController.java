package ru.andef.andefracing.backend.network.controllers.club.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiTags;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

@Tag(name = ApiTags.CLUB_MANAGEMENT)
@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT)
@Validated
@RequiredArgsConstructor
public class ClubManagementController {
    private final ClubManagementService clubManagementService;

    /**
     * Изменение количества симуляторов в выбранном текущим клубе
     */
    @PatchMapping(version = ApiVersions.V1)
    public ResponseEntity<Void> updateCntEquipmentInClub(
            @RequestParam(name = "cntEquipment") @NotNull @Min(1) @Max(10000) Short cntEquipment,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.updateCntEquipmentInClub(principal.id(), principal.clubId(), cntEquipment);
        return ResponseEntity.ok().build();
    }

    /**
     * Открыть клуб
     */
    @PatchMapping(path = "/open", version = ApiVersions.V1)
    public ResponseEntity<Void> openClub(Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.openClub(principal.id(), principal.clubId());
        return ResponseEntity.ok().build();
    }

    /**
     * Закрыть клуб
     */
    @PatchMapping(path = "/close", version = ApiVersions.V1)
    public ResponseEntity<Void> closeClub(Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.closeClub(principal.id(), principal.clubId());
        return ResponseEntity.ok().build();
    }
}