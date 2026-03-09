package ru.andef.andefracing.backend.network.dtos.auth.client;

import ru.andef.andefracing.backend.network.dtos.auth.AuthResponseDto;

/**
 * Ответ от сервера клиенту после успешного входа, регистрации или смены пароля
 */
public class ClientAuthResponseDto extends AuthResponseDto {
    public ClientAuthResponseDto(String jwt) {
        super(jwt);
    }
}