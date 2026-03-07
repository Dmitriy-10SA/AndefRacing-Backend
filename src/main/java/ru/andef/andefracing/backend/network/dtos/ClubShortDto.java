package ru.andef.andefracing.backend.network.dtos;

/**
 * Dto клуба с краткой информацией о клубе
 */
public record ClubShortDto(
        int id,
        String name,
        String phone,
        String email,
        String address,
        short cntEquipment,
        boolean isOpen,
        PhotoDto mainPhoto
) {
}