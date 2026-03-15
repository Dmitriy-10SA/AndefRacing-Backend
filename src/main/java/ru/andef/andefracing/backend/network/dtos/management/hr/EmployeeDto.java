package ru.andef.andefracing.backend.network.dtos.management.hr;

/**
 * DTO - сотрудник
 */
public record EmployeeDto(
        Long id,
        String surname,
        String name,
        String patronymic,
        String phone
) {
}
