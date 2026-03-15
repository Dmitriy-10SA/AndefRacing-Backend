package ru.andef.andefracing.backend.network.dtos.booking;

/**
 * Dto - клиент
 */
public record ClientDto(
        Long id,
        String name,
        String phone
) {
}
