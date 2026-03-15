package ru.andef.andefracing.backend.network.dtos.auth.employee;

import ru.andef.andefracing.backend.network.dtos.auth.LoginDto;

/**
 * Dto для логина сотрудника
 */
public class EmployeeLoginDto extends LoginDto {
    public EmployeeLoginDto(String phone, String password) {
        super(phone, password);
    }
}