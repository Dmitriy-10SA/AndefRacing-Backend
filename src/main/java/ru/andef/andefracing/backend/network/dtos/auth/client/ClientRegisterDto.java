package ru.andef.andefracing.backend.network.dtos.auth.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dto для регистрации клиента
 */
public record ClientRegisterDto(
        @NotBlank(message = "Имя должно быть заполнено")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String name,
        @NotBlank(message = "Номер телефона должен быть заполнен")
        @Pattern(
                regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
                message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
        )
        String phone,
        @NotBlank(message = "Пароль должен быть заполнен")
        @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
        @Pattern(
                regexp = "^(?=.*[a-zа-я])(?=.*[A-ZА-Я])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>/?]).+$",
                message = "Пароль должен содержать хотя бы одну заглавную букву, одну цифру и один спецсимвол"
        )
        String password
) {
}