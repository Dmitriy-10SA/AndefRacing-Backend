package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.management.AddPhotoDto;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_PHOTOS)
@Validated
public class ClubPhotosManagementController {
    /**
     * Добавление фотографии
     */
    @PostMapping
    public ResponseEntity<Void> addPhoto(@RequestBody @Valid AddPhotoDto addPhotoDto) {
        // TODO: Добавить URL фотографии в галерею клуба
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление фотографии
     */
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable long photoId) {
        // TODO: Удалить фотографию из галереи клуба
        return ResponseEntity.ok().build();
    }

    /**
     * Переупорядочивание фотографий
     */
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderPhotos(@RequestBody @Valid @NotNull List<@NotNull Long> orderedPhotoIds) {
        // TODO: Обновить порядок фотографий в галерее
        return ResponseEntity.ok().build();
    }
}