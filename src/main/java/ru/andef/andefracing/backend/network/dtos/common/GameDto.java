package ru.andef.andefracing.backend.network.dtos.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO - игра
 */
public record GameDto(
        short id,
        @NotNull(message = "Необходимо указать название игры")
        @NotBlank(message = "Название должно содержать хотя бы один символ")
        @Size(max = 100, message = "Длина названия игра должна быть не более 100 символов")
        String name,
        @NotNull(message = "Необходимо указать url для фото игры")
        @NotBlank(message = "url должен содержать хотя бы один символ")
        String photoUrl,
        boolean isActive
) {
}
