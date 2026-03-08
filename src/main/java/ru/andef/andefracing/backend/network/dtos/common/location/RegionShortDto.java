package ru.andef.andefracing.backend.network.dtos.common.location;

/**
 * Dto региона (краткая информация)
 */
public record RegionShortDto(
        short id,
        String name
) {
}
