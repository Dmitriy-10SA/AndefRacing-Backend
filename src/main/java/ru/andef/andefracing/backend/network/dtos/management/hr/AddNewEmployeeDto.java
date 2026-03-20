package ru.andef.andefracing.backend.network.dtos.management.hr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;

import java.util.List;

/**
 * DTO для добавления нового сотрудника в клуб
 */
@Getter
public class AddNewEmployeeDto extends AddExistingEmployeeDto {
    @NotNull
    @NotBlank
    @Size(max = 100)
    private final String surname;
    @NotNull
    @NotBlank
    @Size(max = 100)
    private final String name;
    private final String patronymic;

    public AddNewEmployeeDto(
            String phone,
            @Size(min = 1) List<@NotNull EmployeeRole> roles,
            String surname,
            String name,
            String patronymic
    ) {
        super(phone, roles);
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }
}
