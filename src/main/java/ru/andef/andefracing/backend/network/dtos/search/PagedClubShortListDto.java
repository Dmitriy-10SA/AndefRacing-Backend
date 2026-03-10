package ru.andef.andefracing.backend.network.dtos.search;

import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubInfoDto;

import java.util.List;

/**
 * DTO - список клубов (краткая информация о них)
 */
public record PagedClubShortListDto(
        List<ClubInfoDto> content,
        PageInfoDto pageInfoDto
) {
}
