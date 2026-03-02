package ru.andef.andefracing.backend.network.dtos.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Ответ от сервера после успешного входа, регистрации или смены пароля
 */
@Getter
@RequiredArgsConstructor
public abstract class AuthResponseDto {
    private final String jwt;
}