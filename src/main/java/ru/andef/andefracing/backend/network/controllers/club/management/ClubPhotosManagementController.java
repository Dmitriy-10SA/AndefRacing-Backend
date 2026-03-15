package ru.andef.andefracing.backend.network.controllers.club.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiTags;
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
     * Управление фотографиями в клубе
     */
    @PostMapping("/manage")
    public ResponseEntity<Void> managePhotosInClub(
            @RequestBody @Valid @NotNull List<@Valid AddPhotoDto> addPhotoDtos,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.managePhotosInClub(principal.clubId(), addPhotoDtos);
        return ResponseEntity.ok().build();
    }
}