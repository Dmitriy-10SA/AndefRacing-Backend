package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - сотрудник по данному телефону уже зарегистрирован
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class EmployeeWithThisPhoneAlreadyExistsException extends RuntimeException {
    private final String phone;

    public EmployeeWithThisPhoneAlreadyExistsException(String phone) {
        super("Сотрудник с номером телефона " + phone + " уже существует");
        this.phone = phone;
    }
}
