package ru.andef.andefracing.backend.network.dtos.auth.client;

import ru.andef.andefracing.backend.network.dtos.auth.LoginDto;

/**
 * Dto для логина клиента
 */
public class ClientLoginDto extends LoginDto {
    public ClientLoginDto(String phone, String password) {
        super(phone, password);
    }
}