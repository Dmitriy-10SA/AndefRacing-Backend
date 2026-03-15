package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;
import ru.andef.andefracing.backend.network.dtos.search.PriceDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PriceMapper {
    @Mapping(target = "id", expression = "java(price.getId())")
    @Mapping(target = "durationMinutes", expression = "java(price.getDurationMinutes())")
    @Mapping(target = "value", expression = "java(price.getValue())")
    PriceDto toDto(Price price);

    List<PriceDto> toDto(List<Price> prices);

    @Mapping(target = "durationMinutes", expression = "java(addPriceDto.durationMinutes())")
    @Mapping(target = "value", expression = "java(addPriceDto.value())")
    Price toEntity(AddPriceDto addPriceDto);
}
