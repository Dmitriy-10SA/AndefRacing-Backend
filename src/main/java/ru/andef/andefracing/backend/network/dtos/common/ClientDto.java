package ru.andef.andefracing.backend.network.dtos.common;

/**
 * Dto - клиент
 */
public record ClientDto(
        long id,
        String name,
        String phone
) {
}
