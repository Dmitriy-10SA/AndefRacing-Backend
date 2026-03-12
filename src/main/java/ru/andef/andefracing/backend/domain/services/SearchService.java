package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.club.GameRepository;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.common.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.mappers.club.*;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private static final String NAME = "name";

    private final ClubRepository clubRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final GameRepository gameRepository;

    private final ClubMapper clubMapper;
    private final RegionMapper regionMapper;
    private final CityMapper cityMapper;
    private final PhotoMapper photoMapper;
    private final GameMapper gameMapper;
    private final PriceMapper priceMapper;
    private final WorkScheduleMapper workScheduleMapper;

    /**
     * Получение клуба по id или выброс исключения
     */
    private Club findClubByIdOrThrow(int clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Клуб с id " + clubId + " не найден"));
    }

    /**
     * Получение региона по id или выброс исключения
     */
    private Region findRegionByIdOrThrow(short id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Регион с id " + id + " не найден"));
    }

    /**
     * Получение города по id или выброс исключения
     */
    private City findCityByIdOrThrow(short id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Город с id " + id + " не найден"));
    }

    /**
     * Получение всех регионов
     */
    @Transactional(readOnly = true)
    public List<RegionShortDto> getAllRegions() {
        List<Region> regions = regionRepository.findAll(Sort.by(NAME));
        return regionMapper.toShortDto(regions);
    }

    /**
     * Получение всех городов в указанном регионе
     */
    @Transactional(readOnly = true)
    public List<CityShortDto> getAllCitiesInRegion(short regionId) {
        Region region = findRegionByIdOrThrow(regionId);
        List<City> cities = region.getCities();
        return cityMapper.toShortDto(cities);
    }

    /**
     * Получение всех клубов (работающих) в указанном городе с пагинацией
     */
    @Transactional(readOnly = true)
    public PagedClubShortListDto getAllClubsInCity(short cityId, int pageNumber, int pageSize) {
        City city = findCityByIdOrThrow(cityId);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(NAME));
        Page<Club> clubsPage = clubRepository.findAllByCity_IdAndIsOpenTrue(city.getId(), pageRequest);
        List<ClubInfoDto> content = clubMapper.toInfoDto(clubsPage.getContent(), photoMapper);
        PageInfoDto pageInfoDto = new PageInfoDto(
                pageNumber,
                pageSize,
                clubsPage.getTotalElements(),
                clubsPage.getTotalPages(),
                clubsPage.isLast()
        );
        return new PagedClubShortListDto(content, pageInfoDto);
    }

    /**
     * Получение подробной информации о клубе
     */
    @Transactional(readOnly = true)
    public ClubFullInfoDto getClubFullInfo(int clubId) {
        Club club = findClubByIdOrThrow(clubId);
        List<Game> games = gameRepository.findAllActiveGamesInClub(clubId);
        return clubMapper.toFullInfoDto(club, games, photoMapper, gameMapper, priceMapper, workScheduleMapper);
    }
}
