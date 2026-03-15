package ru.andef.andefracing.backend.network.dtos.common;

/**
 * DTO - игра
 */
public record GameDto(
        Short id,
        String name,
        String photoUrl,
        Boolean isActive
) {
}
