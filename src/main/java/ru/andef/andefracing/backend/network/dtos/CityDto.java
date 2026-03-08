package ru.andef.andefracing.backend.network.dtos;

/**
 * Dto города
 */
public record CityDto(
        short id,
        String name,
        RegionShortDto region
) {
}
