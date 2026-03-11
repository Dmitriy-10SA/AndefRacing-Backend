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
    @Mapping(target = "id", expression = "java(game.getId())")
    @Mapping(target = "durationMinutes", expression = "java(game.getDurationMinutes())")
    @Mapping(target = "value", expression = "java(game.getValue())")
    PriceDto toDto(Price price);

    List<PriceDto> toDto(List<Price> prices);

    @Mapping(target = "durationMinutes", expression = "java(game.durationMinutes())")
    @Mapping(target = "value", expression = "java(game.value())")
    Price toEntity(AddPriceDto addPriceDto);
}
