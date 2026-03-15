package ru.andef.andefracing.backend.network.dtos.common.club;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Dto клуба с краткой информацией о клубе
 */
@Getter
@RequiredArgsConstructor
public class ClubShortDto {
    private final Integer id;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final Short cntEquipment;
    private final Boolean isOpen;
}