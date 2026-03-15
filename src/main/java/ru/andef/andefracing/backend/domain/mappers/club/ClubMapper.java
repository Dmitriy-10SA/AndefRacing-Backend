package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.*;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubShortDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.FavoriteClubShortDto;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;

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

    @Mapping(target = "id", expression = "java(club.getId())")
    @Mapping(target = "name", expression = "java(club.getName())")
    @Mapping(target = "phone", expression = "java(club.getPhone())")
    @Mapping(target = "email", expression = "java(club.getEmail())")
    @Mapping(target = "address", expression = "java(club.getAddress())")
    @Mapping(target = "cntEquipment", expression = "java(club.getCntEquipment())")
    @Mapping(target = "isOpen", expression = "java(club.isOpen())")
    @Mapping(target = "mainPhoto", expression = "java(photoMapper.toDto(club.getPhotos().get(0)))")
    ClubInfoDto toInfoDto(Club club, @Context PhotoMapper photoMapper);

    List<ClubInfoDto> toInfoDto(List<Club> clubs, @Context PhotoMapper photoMapper);

    @Mapping(target = "id", expression = "java(club.getId())")
    @Mapping(target = "name", expression = "java(club.getName())")
    @Mapping(target = "phone", expression = "java(club.getPhone())")
    @Mapping(target = "email", expression = "java(club.getEmail())")
    @Mapping(target = "address", expression = "java(club.getAddress())")
    @Mapping(target = "cntEquipment", expression = "java(club.getCntEquipment())")
    @Mapping(target = "isOpen", expression = "java(club.isOpen())")
    @Mapping(target = "photos", expression = "java(photoMapper.toDto(club.getPhotos()))")
    @Mapping(target = "games", expression = "java(gameMapper.toDto(games))")
    @Mapping(target = "prices", expression = "java(priceMapper.toDto(club.getPrices()))")
    @Mapping(target = "workSchedules", expression = "java(workScheduleMapper.toDto(club.getWorkSchedules()))")
    ClubFullInfoDto toFullInfoDto(
            Club club,
            List<Game> games,
            @Context PhotoMapper photoMapper,
            @Context GameMapper gameMapper,
            @Context PriceMapper priceMapper,
            @Context WorkScheduleMapper workScheduleMapper
    );

    @Mapping(target = "id", expression = "java(club.getId())")
    @Mapping(target = "name", expression = "java(club.getName())")
    @Mapping(target = "phone", expression = "java(club.getPhone())")
    @Mapping(target = "email", expression = "java(club.getEmail())")
    @Mapping(target = "address", expression = "java(club.getAddress())")
    @Mapping(target = "cntEquipment", expression = "java(club.getCntEquipment())")
    @Mapping(target = "isOpen", expression = "java(club.isOpen())")
    ClubShortDto toShortDto(Club club);
}