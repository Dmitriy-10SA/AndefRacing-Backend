package ru.andef.andefracing.backend.domain.exceptions.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - клиент по данному телефону уже зарегистрирован
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ClientWithThisPhoneAlreadyExistsException extends RuntimeException {
    private final String phone;

    public ClientWithThisPhoneAlreadyExistsException(String phone) {
        super("Клиент с номером телефона " + phone + " уже существует");
        this.phone = phone;
    }
}
