package ru.andef.andefracing.backend.network.dtos.common;

/**
 * Dto фотографии
 */
public record PhotoDto(
        Long id,
        String url,
        Short sequenceNumber
) {
}
