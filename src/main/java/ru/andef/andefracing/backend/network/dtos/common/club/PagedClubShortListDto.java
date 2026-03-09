package ru.andef.andefracing.backend.network.dtos.common.club;

import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;

import java.util.List;

public record PagedClubShortListDto(
        List<ClubInfoDto> content,
        PageInfoDto pageInfoDto
) {
}
