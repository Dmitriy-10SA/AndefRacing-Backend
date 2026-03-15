package ru.andef.andefracing.backend.domain.services.search;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.CacheConfig;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationSearchService {
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    private final RegionMapper regionMapper;
    private final CityMapper cityMapper;

    /**
     * Получение региона по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Region findRegionById(short id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Регион с id " + id + " не найден"));
    }

    /**
     * Получение города по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public City findCityById(short id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Город с id " + id + " не найден"));
    }

    /**
     * Получение всех регионов в которых есть открытые клубы
     */
    @Cacheable(value = CacheConfig.CacheNames.REGIONS, key = "'all'")
    @Transactional(readOnly = true)
    public List<RegionShortDto> getAllRegionsWithOpenClubs() {
        List<Region> regions = regionRepository.findAllRegionsWithOpenClubs();
        return regionMapper.toShortDto(regions);
    }

    /**
     * Получение всех городов в указанном регионе, в которых есть открытые клубы
     */
    @Cacheable(value = CacheConfig.CacheNames.CITIES, key = "#regionId")
    @Transactional(readOnly = true)
    public List<CityShortDto> getAllCitiesInRegionWithOpenClubs(short regionId) {
        Region region = findRegionById(regionId);
        List<City> cities = cityRepository.findAllCitiesInRegionWithOpenClubs(region.getId());
        return cityMapper.toShortDto(cities);
    }
}