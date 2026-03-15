package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GameMapper {
    @Mapping(target = "id", expression = "java(game.getId())")
    @Mapping(target = "name", expression = "java(game.getName())")
    @Mapping(target = "photoUrl", expression = "java(game.getPhotoUrl())")
    @Mapping(target = "isActive", expression = "java(game.isActive())")
    GameDto toDto(Game game);

    List<GameDto> toDto(List<Game> games);
}
