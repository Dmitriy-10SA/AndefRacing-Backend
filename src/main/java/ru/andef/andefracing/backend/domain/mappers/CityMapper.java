package ru.andef.andefracing.backend.domain.mappers;

import org.mapstruct.*;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.network.dtos.common.location.CityDto;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {RegionMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CityMapper {
    @Mapping(target = "id", expression = "java(city.getId())")
    @Mapping(target = "name", expression = "java(city.getName())")
    @Mapping(target = "region", expression = "java(regionMapper.toShortDto(city.getRegion()))")
    CityDto toDto(City city, @Context RegionMapper regionMapper);
}