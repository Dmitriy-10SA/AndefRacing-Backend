package ru.andef.andefracing.backend.network.dtos.profile.client;

import ru.andef.andefracing.backend.network.dtos.profile.PersonalInfoDto;

/**
 * Dto для персональных данных клиента
 */
public class ClientPersonalInfoDto extends PersonalInfoDto {
    public ClientPersonalInfoDto(String phone, String name) {
        super(phone, name);
    }
}
