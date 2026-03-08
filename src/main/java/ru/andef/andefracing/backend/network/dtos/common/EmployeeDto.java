package ru.andef.andefracing.backend.network.dtos.common;

public record EmployeeDto(
        long id,
        String surname,
        String name,
        String patronymic,
        String phone
) {
}
