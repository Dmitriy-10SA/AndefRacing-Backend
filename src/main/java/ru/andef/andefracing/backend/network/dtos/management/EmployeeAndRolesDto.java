package ru.andef.andefracing.backend.network.dtos.management;

import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;

import java.util.List;

/**
 * DTO - сотрудник и его роли
 */
public record EmployeeAndRolesDto(
        EmployeeDto employeeDto,
        List<EmployeeRole> roles
) {
}
