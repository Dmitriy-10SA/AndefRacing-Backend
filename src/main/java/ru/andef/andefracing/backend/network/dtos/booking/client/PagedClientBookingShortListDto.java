package ru.andef.andefracing.backend.network.dtos.booking.client;

import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;

import java.util.List;

/**
 * Список бронирований для клиента с пагинацией
 */
public record PagedClientBookingShortListDto(
        List<ClientBookingShortDto> content,
        PageInfoDto pageInfo
) {
}
