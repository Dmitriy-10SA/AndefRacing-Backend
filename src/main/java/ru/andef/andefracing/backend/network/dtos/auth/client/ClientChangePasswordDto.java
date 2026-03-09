package ru.andef.andefracing.backend.network.dtos.auth.client;

import ru.andef.andefracing.backend.network.dtos.auth.ChangePasswordDto;

/**
 * Dto для смены пароля клиентом
 */
public class ClientChangePasswordDto extends ChangePasswordDto {
    public ClientChangePasswordDto(String phone, String password) {
        super(phone, password);
    }
}