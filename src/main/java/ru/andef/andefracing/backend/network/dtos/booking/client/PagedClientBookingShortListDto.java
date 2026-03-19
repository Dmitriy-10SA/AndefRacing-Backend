package ru.andef.andefracing.backend.network.dtos.booking.client;

import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;

import java.util.List;

public record PagedClientBookingShortListDto(
        List<ClientBookingShortDto> content,
        PageInfoDto pageInfo
) {
}
