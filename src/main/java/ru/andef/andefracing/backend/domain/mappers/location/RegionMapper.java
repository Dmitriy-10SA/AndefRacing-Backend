package ru.andef.andefracing.backend.domain.mappers.location;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RegionMapper {
    @Mapping(target = "id", expression = "java(region.getId())")
    @Mapping(target = "name", expression = "java(region.getName())")
    RegionShortDto toShortDto(Region region);

    List<RegionShortDto> toShortDto(List<Region> regions);
}