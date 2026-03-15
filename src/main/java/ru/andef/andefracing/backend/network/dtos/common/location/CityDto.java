package ru.andef.andefracing.backend.network.dtos.common.location;

import lombok.Getter;

/**
 * Dto города
 */
@Getter
public class CityDto extends CityShortDto {
    private final RegionShortDto region;

    public CityDto(Short id, String name, RegionShortDto region) {
        super(id, name);
        this.region = region;
    }
}
