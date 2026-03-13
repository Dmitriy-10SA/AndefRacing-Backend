package ru.andef.andefracing.backend.domain.services.search;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
@Transactional
class LocationSearchServiceTest {
    private final LocationSearchService locationSearchService;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final ClubRepository clubRepository;

    @Autowired
    public LocationSearchServiceTest(
            LocationSearchService locationSearchService,
            RegionRepository regionRepository,
            CityRepository cityRepository,
            ClubRepository clubRepository
    ) {
        this.locationSearchService = locationSearchService;
        this.regionRepository = regionRepository;
        this.cityRepository = cityRepository;
        this.clubRepository = clubRepository;
    }

    private Region createRegion(String name) {
        Region region = new Region((short) 0, name, new ArrayList<>());
        return regionRepository.save(region);
    }

    private City createCity(Region region, String name) {
        City city = new City((short) 0, region, name);
        return cityRepository.save(city);
    }

    private void createClub(City city, String name, boolean isOpen) {
        Club club = new Club(
                0,
                city,
                name,
                "+7-000-000-00-00",
                "test@example.com",
                "Test address",
                (short) 10,
                isOpen,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        clubRepository.save(club);
    }

    @Test
    void findRegionByIdReturnsRegionWhenExists() {
        // Arrange
        Region region = createRegion("Test Region");

        // Act
        Region result = locationSearchService.findRegionById(region.getId());

        // Assert
        assertNotNull(result);
        assertEquals(region.getId(), result.getId());
        assertEquals(region.getName(), result.getName());
    }

    @Test
    void findRegionByIdThrowsExceptionWhenRegionNotFound() {
        // Arrange
        short nonExistentId = 999;

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                locationSearchService.findRegionById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    void findCityByIdReturnsCityWhenExists() {
        // Arrange
        Region region = createRegion("Test Region");
        City city = createCity(region, "Test City");

        // Act
        City result = locationSearchService.findCityById(city.getId());

        // Assert
        assertNotNull(result);
        assertEquals(city.getId(), result.getId());
        assertEquals(city.getName(), result.getName());
        assertEquals(region.getId(), result.getRegion().getId());
    }

    @Test
    void findCityByIdThrowsExceptionWhenCityNotFound() {
        // Arrange
        short nonExistentId = 999;

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                locationSearchService.findCityById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    void getAllRegionsWithOpenClubsReturnsOnlyRegionsWithOpenClubs() {
        // Arrange
        Region regionWithOpenClub = createRegion("Region with Open Club");
        Region regionWithClosedClub = createRegion("Region with Closed Club");

        City cityWithOpenClub = createCity(regionWithOpenClub, "City with Open Club");
        City cityWithClosedClub = createCity(regionWithClosedClub, "City with Closed Club");

        createClub(cityWithOpenClub, "Open Club", true);
        createClub(cityWithClosedClub, "Closed Club", false);

        // Act
        List<RegionShortDto> result = locationSearchService.getAllRegionsWithOpenClubs();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(regionWithOpenClub.getId(), result.get(0).id());
        assertEquals(regionWithOpenClub.getName(), result.get(0).name());
    }

    @Test
    void getAllRegionsWithOpenClubsReturnsEmptyListWhenNoOpenClubs() {
        // Arrange
        Region region = createRegion("Region");
        City city = createCity(region, "City");
        createClub(city, "Closed Club", false);

        // Act
        List<RegionShortDto> result = locationSearchService.getAllRegionsWithOpenClubs();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRegionsWithOpenClubsReturnsMultipleRegions() {
        // Arrange
        Region region1 = createRegion("Region 1");
        Region region2 = createRegion("Region 2");

        City city1 = createCity(region1, "City 1");
        City city2 = createCity(region2, "City 2");

        createClub(city1, "Open Club 1", true);
        createClub(city2, "Open Club 2", true);

        // Act
        List<RegionShortDto> result = locationSearchService.getAllRegionsWithOpenClubs();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllCitiesInRegionWithOpenClubsReturnsOnlyCitiesWithOpenClubs() {
        // Arrange
        Region region = createRegion("Test Region");
        City cityWithOpenClub = createCity(region, "City with Open Club");
        City cityWithClosedClub = createCity(region, "City with Closed Club");

        createClub(cityWithOpenClub, "Open Club", true);
        createClub(cityWithClosedClub, "Closed Club", false);

        // Act
        List<CityShortDto> result = locationSearchService.getAllCitiesInRegionWithOpenClubs(region.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cityWithOpenClub.getId(), result.get(0).getId());
        assertEquals(cityWithOpenClub.getName(), result.get(0).getName());
    }

    @Test
    void getAllCitiesInRegionWithOpenClubsReturnsEmptyListWhenNoOpenClubs() {
        // Arrange
        Region region = createRegion("Test Region");
        City city = createCity(region, "City");
        createClub(city, "Closed Club", false);

        // Act
        List<CityShortDto> result = locationSearchService.getAllCitiesInRegionWithOpenClubs(region.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCitiesInRegionWithOpenClubsReturnsMultipleCities() {
        // Arrange
        Region region = createRegion("Test Region");
        City city1 = createCity(region, "City 1");
        City city2 = createCity(region, "City 2");

        createClub(city1, "Open Club 1", true);
        createClub(city2, "Open Club 2", true);

        // Act
        List<CityShortDto> result = locationSearchService.getAllCitiesInRegionWithOpenClubs(region.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllCitiesInRegionWithOpenClubsThrowsExceptionWhenRegionNotFound() {
        // Arrange
        short nonExistentRegionId = 999;

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                locationSearchService.getAllCitiesInRegionWithOpenClubs(nonExistentRegionId)
        );
    }
}