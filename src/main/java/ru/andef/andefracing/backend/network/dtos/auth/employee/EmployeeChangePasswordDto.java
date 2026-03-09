package ru.andef.andefracing.backend.network.dtos.auth.employee;

import ru.andef.andefracing.backend.network.dtos.auth.ChangePasswordDto;

/**
 * Dto для смены пароля сотрудником
 */
public class EmployeeChangePasswordDto extends ChangePasswordDto {
    public EmployeeChangePasswordDto(String phone, String password) {
        super(phone, password);
    }
}