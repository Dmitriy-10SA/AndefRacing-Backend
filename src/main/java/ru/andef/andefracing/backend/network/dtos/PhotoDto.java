package ru.andef.andefracing.backend.network.dtos;

/**
 * Dto фотографии
 */
public record PhotoDto(
        int id,
        String url,
        short sequenceNumber
) {
}
