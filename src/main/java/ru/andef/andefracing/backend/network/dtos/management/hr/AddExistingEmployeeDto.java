package ru.andef.andefracing.backend.network.dtos.management.hr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;

import java.util.List;

/**
 * DTO для добавления существующего сотрудника
 */
@Getter
@RequiredArgsConstructor
public class AddExistingEmployeeDto {
    @NotNull
    @NotBlank
    @Pattern(
            regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
            message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
    )
    private final String phone;
    @NotNull
    private final List<@NotNull EmployeeRole> roles;
}
