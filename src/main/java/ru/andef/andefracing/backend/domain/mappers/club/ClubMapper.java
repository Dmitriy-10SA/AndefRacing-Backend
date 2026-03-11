package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.*;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.FavoriteClubShortDto;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CityMapper.class, PhotoMapper.class},
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

    @Mapping(target = "id", expression = "java(club.getId())")
    @Mapping(target = "name", expression = "java(club.getName())")
    @Mapping(target = "phone", expression = "java(club.getPhone())")
    @Mapping(target = "email", expression = "java(club.getEmail())")
    @Mapping(target = "address", expression = "java(club.getAddress())")
    @Mapping(target = "cntEquipment", expression = "java(club.getCntEquipment())")
    @Mapping(target = "isOpen", expression = "java(club.isOpen())")
    @Mapping(target = "city", expression = "java(cityMapper.toDto(club.getCity(), regionMapper))")
    @Mapping(target = "mainPhoto", expression = "java(photoMapper.toDto(club.getPhotos().get(0)))")
    FavoriteClubShortDto toFavoriteClubShortDto(
            Club club,
            @Context CityMapper cityMapper,
            @Context RegionMapper regionMapper,
            @Context PhotoMapper photoMapper
    );

    List<FavoriteClubShortDto> toFavoriteClubShortDto(
            List<Club> clubs,
            @Context CityMapper cityMapper,
            @Context RegionMapper regionMapper,
            @Context PhotoMapper photoMapper
    );
}