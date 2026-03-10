package ru.andef.andefracing.backend.domain.exceptions.auth.employee;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - сотрудник с таким телефоном не найден
 */
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmployeeWithThisPhoneNotFoundException extends RuntimeException {
    private final String phone;

    public EmployeeWithThisPhoneNotFoundException(String phone) {
        super("Сотрудник с номером телефона " + phone + " не найден");
        this.phone = phone;
    }
}
