package ru.andef.andefracing.backend.network.dtos.common;

public record GameDto(
        short id,
        String name,
        String photoUrl,
        boolean isActive
) {
}
