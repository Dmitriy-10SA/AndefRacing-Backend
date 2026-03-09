package ru.andef.andefracing.backend.network.dtos.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GameDto(
        short id,
        @NotNull @NotBlank @Size(max = 100) String name,
        @NotNull @NotBlank String photoUrl,
        boolean isActive
) {
}
