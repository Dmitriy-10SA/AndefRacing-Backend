package ru.andef.andefracing.backend.network.controllers.club.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiTags;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.management.AddPhotoDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.util.List;

@Tag(name = ApiTags.CLUB_MANAGEMENT)
@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_PHOTOS)
@Validated
@RequiredArgsConstructor
public class ClubPhotosManagementController {
    private final ClubManagementService clubManagementService;

    /**
     * Добавление фотографии в клуб
     */
    @PostMapping(version = ApiVersions.V1)
    public ResponseEntity<Void> addPhotoInClub(
            @RequestBody @Valid AddPhotoDto addPhotoDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.addPhotoInClub(principal.clubId(), addPhotoDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление фотографии из клуба
     */
    @DeleteMapping(path = "/{photoId}", version = ApiVersions.V1)
    public ResponseEntity<Void> deletePhotoFromClub(@PathVariable long photoId, Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.deletePhotoFromClub(principal.clubId(), photoId);
        return ResponseEntity.ok().build();
    }

    /**
     * Переупорядочивание фотографий в клубе
     */
    @PatchMapping(path = "/reorder", version = ApiVersions.V1)
    public ResponseEntity<Void> reorderPhotosInClub(
            @RequestBody @Valid @NotNull List<@NotNull Long> orderedPhotoIds,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.reorderPhotosInClub(principal.clubId(), orderedPhotoIds);
        return ResponseEntity.ok().build();
    }
}