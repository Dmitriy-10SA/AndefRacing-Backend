package ru.andef.andefracing.backend.network.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Dto для логина
 */
@Getter
@RequiredArgsConstructor
public abstract class LoginDto {
    @NotBlank(message = "Номер телефона должен быть заполнен")
    @Pattern(
            regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
            message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
    )
    private final String phone;
    @NotBlank(message = "Пароль должен быть заполнен")
    private final String password;
}