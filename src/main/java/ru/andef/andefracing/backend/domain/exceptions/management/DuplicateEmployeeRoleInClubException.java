package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;

/**
 * Ошибка - дубликат роли в клубе у сотрудника
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmployeeRoleInClubException extends RuntimeException {
    private final long id;
    private final EmployeeRole role;

    public DuplicateEmployeeRoleInClubException(long id, EmployeeRole role) {
        super("Сотрудник с id " + id + " уже имеет роль " + role.getRu());
        this.id = id;
        this.role = role;
    }
}
