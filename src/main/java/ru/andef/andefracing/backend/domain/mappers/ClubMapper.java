package ru.andef.andefracing.backend.domain.mappers;

import org.mapstruct.*;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CityMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ClubMapper {
    @Mapping(target = "id", expression = "java(club.getId())")
    @Mapping(target = "name", expression = "java(club.getName())")
    @Mapping(target = "phone", expression = "java(club.getPhone())")
    @Mapping(target = "email", expression = "java(club.getEmail())")
    @Mapping(target = "address", expression = "java(club.getAddress())")
    @Mapping(target = "cntEquipment", expression = "java(club.getCntEquipment())")
    @Mapping(target = "isOpen", expression = "java(club.isOpen())")
    @Mapping(target = "city", expression = "java(cityMapper.toDto(club.getCity(), regionMapper))")
    EmployeeClubDto toEmployeeClubDto(Club club, @Context CityMapper cityMapper, @Context RegionMapper regionMapper);

    List<EmployeeClubDto> toEmployeeClubDto(
            List<Club> clubs,
            @Context CityMapper cityMapper,
            @Context RegionMapper regionMapper
    );
}