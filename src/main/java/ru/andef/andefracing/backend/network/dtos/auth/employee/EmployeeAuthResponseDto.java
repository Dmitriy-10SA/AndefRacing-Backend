package ru.andef.andefracing.backend.network.dtos.auth.employee;

import ru.andef.andefracing.backend.network.dtos.auth.AuthResponseDto;

/**
 * Ответ от сервера сотруднику после успешного входа, регистрации или смены пароля
 */
public class EmployeeAuthResponseDto extends AuthResponseDto {
    public EmployeeAuthResponseDto(String jwt) {
        super(jwt);
    }
}