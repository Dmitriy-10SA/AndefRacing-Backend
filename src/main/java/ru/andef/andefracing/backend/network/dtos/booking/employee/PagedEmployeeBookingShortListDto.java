package ru.andef.andefracing.backend.network.dtos.booking.employee;

import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;

import java.util.List;

public record PagedEmployeeBookingShortListDto(
        List<EmployeeBookingShortDto> content,
        PageInfoDto pageInfo
) {
}
