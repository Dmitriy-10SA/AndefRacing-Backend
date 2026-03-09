package ru.andef.andefracing.backend.network.dtos.management;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddPhotoDto(
        @NotNull @NotBlank String url,
        @Min(1) short sequenceNumber
) {
}
