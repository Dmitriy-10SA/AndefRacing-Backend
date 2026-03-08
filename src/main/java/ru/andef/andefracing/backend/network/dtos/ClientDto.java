package ru.andef.andefracing.backend.network.dtos;

/**
 * Dto - клиент
 */
public record ClientDto(
        long id,
        String name,
        String phone
) {
}
