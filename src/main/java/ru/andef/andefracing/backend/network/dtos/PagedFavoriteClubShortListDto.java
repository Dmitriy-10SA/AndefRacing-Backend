package ru.andef.andefracing.backend.network.dtos;

import java.util.List;

/**
 * Dto списка избранных клубов клиента (краткая информация) с пагинацией
 */
public record PagedFavoriteClubShortListDto(
        List<FavoriteClubShortDto> content,
        PageInfoDto pageInfo
) {
}