package ru.andef.andefracing.backend.network.dtos.common;

import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;

import java.util.List;

public record EmployeeAndRolesDto(
        EmployeeDto employeeDto,
        List<EmployeeRole> roles
) {
}
