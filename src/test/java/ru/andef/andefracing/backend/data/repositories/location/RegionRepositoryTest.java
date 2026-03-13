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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
class RegionRepositoryTest {

    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final ClubRepository clubRepository;

    @Autowired
    RegionRepositoryTest(
            RegionRepository regionRepository,
            CityRepository cityRepository,
            ClubRepository clubRepository
    ) {
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
    void findAllRegionsWithOpenClubsReturnsOnlyRegionsWithAtLeastOneOpenClubOrderedByName() {
        Region region1 = createRegion("Alpha Region");
        Region region2 = createRegion("Beta Region");
        Region region3 = createRegion("Gamma Region");

        City city1 = createCity(region1, "City1");
        City city2 = createCity(region2, "City2");
        City city3 = createCity(region3, "City3");

        createClub(city1, "Club1", true);
        createClub(city2, "Club2", false);
        createClub(city3, "Club3", true);

        List<Region> result = regionRepository.findAllRegionsWithOpenClubs();

        assertEquals(2, result.size());
        assertEquals(region1.getId(), result.get(0).getId());
        assertEquals(region3.getId(), result.get(1).getId());
        List<Short> ids = result.stream().map(Region::getId).toList();
        assertFalse(ids.contains(region2.getId()));
    }
}

