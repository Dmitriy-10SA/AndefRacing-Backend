package ru.andef.andefracing.backend.data.repositories.location;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
class CityRepositoryTest {

    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final ClubRepository clubRepository;

    @Autowired
    CityRepositoryTest(
            CityRepository cityRepository,
            RegionRepository regionRepository,
            ClubRepository clubRepository
    ) {
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
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
    void findAllCitiesInRegionWithOpenClubsReturnsOnlyCitiesFromRegionWithOpenClubsOrderedByName() {
        Region region1 = createRegion("Region1");
        Region region2 = createRegion("Region2");

        City city1 = createCity(region1, "Alpha City");
        City city2 = createCity(region1, "Beta City");
        City city3 = createCity(region2, "Gamma City");

        createClub(city1, "Club1", true);
        createClub(city2, "Club2", false);
        createClub(city3, "Club3", true);

        List<City> result = cityRepository.findAllCitiesInRegionWithOpenClubs(region1.getId());

        assertEquals(1, result.size());
        assertEquals(city1.getId(), result.get(0).getId());
        assertTrue(result.stream().noneMatch(city -> city.getId() == city2.getId()));
        assertTrue(result.stream().noneMatch(city -> city.getId() == city3.getId()));
    }
}

