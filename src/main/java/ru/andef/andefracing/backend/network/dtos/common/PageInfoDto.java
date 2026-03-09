package ru.andef.andefracing.backend.network.dtos.common;

/**
 * Информация о странице
 */
public record PageInfoDto(
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast
) {
}