package ru.andef.andefracing.backend.network.dtos;

import java.util.List;

/**
 * Dto списка клубов (краткая информация о клубе) с пагинацией
 */
public record PagedClubShortListDto(
        List<ClubShortDto> content,
        PageInfoDto pageInfo
) {
}