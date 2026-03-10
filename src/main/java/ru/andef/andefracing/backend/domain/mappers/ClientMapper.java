package ru.andef.andefracing.backend.domain.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClientMapper {
    @Mapping(target = "name", expression = "java(registerDto.name())")
    @Mapping(target = "phone", expression = "java(registerDto.phone())")
    @Mapping(target = "password", expression = "java(registerDto.password())")
    @Mapping(target = "blocked", expression = "java(false)")
    Client toEntity(ClientRegisterDto registerDto);
}