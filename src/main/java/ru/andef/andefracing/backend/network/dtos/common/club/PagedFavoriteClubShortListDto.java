package ru.andef.andefracing.backend.network.dtos.common.club;

import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;

import java.util.List;

/**
 * Dto списка избранных клубов клиента (краткая информация) с пагинацией
 */
public record PagedFavoriteClubShortListDto(
        List<FavoriteClubShortDto> content,
        PageInfoDto pageInfo
) {
}