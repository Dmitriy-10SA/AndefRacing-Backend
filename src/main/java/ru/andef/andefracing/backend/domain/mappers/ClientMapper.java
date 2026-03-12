package ru.andef.andefracing.backend.domain.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;
import ru.andef.andefracing.backend.network.dtos.booking.ClientDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClientMapper {
    @Mapping(target = "name", expression = "java(registerDto.name())")
    @Mapping(target = "phone", expression = "java(registerDto.phone())")
    @Mapping(target = "password", expression = "java(registerDto.password())")
    @Mapping(target = "blocked", expression = "java(false)")
    Client toEntity(ClientRegisterDto registerDto);

    @Mapping(target = "phone", expression = "java(client.getPhone())")
    @Mapping(target = "name", expression = "java(client.getName())")
    ClientPersonalInfoDto toPersonalInfoDto(Client client);

    @Mapping(target = "id", expression = "java(client.getId())")
    @Mapping(target = "name", expression = "java(client.getName())")
    @Mapping(target = "phone", expression = "java(client.getPhone())")
    ClientDto toDto(Client client);
}