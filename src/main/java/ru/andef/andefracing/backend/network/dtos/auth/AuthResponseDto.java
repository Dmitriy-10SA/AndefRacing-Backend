package ru.andef.andefracing.backend.network.dtos.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientAuthResponseDto;

/**
 * Ответ от сервера после успешного входа, регистрации или смены пароля
 */
@Getter
@RequiredArgsConstructor
public abstract class AuthResponseDto {
    private final String jwt;
}
