package ru.andef.andefracing.backend.network.dtos.management;

/**
 * DTO - сотрудник
 */
public record EmployeeDto(
        long id,
        String surname,
        String name,
        String patronymic,
        String phone
) {
}
