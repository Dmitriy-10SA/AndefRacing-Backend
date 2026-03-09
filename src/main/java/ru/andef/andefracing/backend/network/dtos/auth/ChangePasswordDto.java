package ru.andef.andefracing.backend.network.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Dto для смены пароля
 */
@Getter
@RequiredArgsConstructor
public abstract class ChangePasswordDto {
    @NotBlank(message = "Номер телефона должен быть заполнен")
    @Pattern(
            regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
            message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
    )
    private final String phone;
    @NotBlank(message = "Пароль должен быть заполнен")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>/?]).+$",
            message = "Пароль должен содержать хотя бы одну заглавную букву, одну цифру и один спецсимвол"
    )
    private final String password;
}