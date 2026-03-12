package ru.andef.andefracing.backend.network.dtos.common;

/**
 * Dto фотографии
 */
public record PhotoDto(
        long id,
        String url,
        short sequenceNumber
) {
}
