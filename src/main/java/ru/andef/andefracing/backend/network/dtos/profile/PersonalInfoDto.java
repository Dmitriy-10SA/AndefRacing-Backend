package ru.andef.andefracing.backend.network.dtos.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Dto для персональных данных
 */
@Getter
@RequiredArgsConstructor
public abstract class PersonalInfoDto {
    private final String phone;
    private final String name;
}