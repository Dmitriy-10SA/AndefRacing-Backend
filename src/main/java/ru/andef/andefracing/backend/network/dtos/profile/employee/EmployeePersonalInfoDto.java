package ru.andef.andefracing.backend.network.dtos.profile.employee;

import lombok.Getter;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.network.dtos.profile.PersonalInfoDto;

import java.util.List;

/**
 * Dto для персональных данных работника
 */
@Getter
public class EmployeePersonalInfoDto extends PersonalInfoDto {
    private final String surname;
    private final String patronymic;
    private final List<EmployeeRole> roles;

    public EmployeePersonalInfoDto(
            String phone,
            String name,
            String surname,
            String patronymic,
            List<EmployeeRole> roles
    ) {
        super(phone, name);
        this.surname = surname;
        this.patronymic = patronymic;
        this.roles = roles;
    }
}