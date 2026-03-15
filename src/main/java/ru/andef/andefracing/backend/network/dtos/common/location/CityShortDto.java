package ru.andef.andefracing.backend.network.dtos.common.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Dto для хранения краткой информации о городе
 */
@Getter
@RequiredArgsConstructor
public class CityShortDto {
    private final Short id;
    private final String name;
}
