package ru.andef.andefracing.backend.network.dtos.booking;

/**
 * Dto - клиент
 */
public record ClientDto(
        long id,
        String name,
        String phone
) {
}
