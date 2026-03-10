package ru.andef.andefracing.backend.network.dtos.management;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для добавления фотографии
 */
public record AddPhotoDto(
        @NotNull(message = "Необходимо указать url для фото")
        @NotBlank(message = "url не может быть пустым")
        String url,
        short sequenceNumber
) {
}
