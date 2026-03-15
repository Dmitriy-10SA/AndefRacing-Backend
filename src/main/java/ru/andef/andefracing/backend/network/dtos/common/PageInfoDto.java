package ru.andef.andefracing.backend.network.dtos.common;

/**
 * Информация о странице
 */
public record PageInfoDto(
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isLast
) {
}