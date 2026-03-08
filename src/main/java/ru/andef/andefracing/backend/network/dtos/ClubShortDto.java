package ru.andef.andefracing.backend.network.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Dto клуба с краткой информацией о клубе
 */
@Getter
@RequiredArgsConstructor
public abstract class ClubShortDto {
    private final int id;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final short cntEquipment;
    private final boolean isOpen;
}