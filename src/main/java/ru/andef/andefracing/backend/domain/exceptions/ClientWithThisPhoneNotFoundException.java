package ru.andef.andefracing.backend.domain.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - клиент с таким телефоном не найден
 */
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClientWithThisPhoneNotFoundException extends RuntimeException {
    private final String phone;

    public ClientWithThisPhoneNotFoundException(String phone) {
        super("Клиент с номером телефона " + phone + " не найден");
        this.phone = phone;
    }
}
