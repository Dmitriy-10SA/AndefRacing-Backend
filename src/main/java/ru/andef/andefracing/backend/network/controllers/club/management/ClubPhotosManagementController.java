package ru.andef.andefracing.backend.network.controllers.club.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiTags;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
            @RequestPart(value = "photos") List<MultipartFile> files,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (files.size() == 1 && files.get(0).isEmpty()) {
            try {
                clubManagementService.managePhotosInClub(principal.clubId(), Collections.emptyList());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok().build();
        }
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (!Objects.equals(file.getContentType(), "image/jpeg")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).build();
            }
        }
        try {
            clubManagementService.managePhotosInClub(principal.clubId(), files);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}