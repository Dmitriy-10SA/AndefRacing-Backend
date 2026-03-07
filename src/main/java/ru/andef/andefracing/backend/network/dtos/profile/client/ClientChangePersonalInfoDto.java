package ru.andef.andefracing.backend.network.dtos.profile.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dto для изменения персональных данных клиента
 */
public record ClientChangePersonalInfoDto(
        @NotBlank(message = "Имя должно быть заполнено")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String name,
        @NotBlank(message = "Номер телефона должен быть заполнен")
        @Pattern(
                regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
                message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
        )
        String phone
) {
}
