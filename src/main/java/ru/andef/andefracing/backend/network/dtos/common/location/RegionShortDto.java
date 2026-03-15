package ru.andef.andefracing.backend.network.dtos.common.location;

/**
 * Dto региона (краткая информация)
 */
public record RegionShortDto(
        Short id,
        String name
) {
}
