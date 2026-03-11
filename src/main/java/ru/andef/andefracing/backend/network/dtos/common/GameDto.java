package ru.andef.andefracing.backend.network.dtos.common;

/**
 * DTO - игра
 */
public record GameDto(
        short id,
        String name,
        String photoUrl,
        boolean isActive
) {
}
