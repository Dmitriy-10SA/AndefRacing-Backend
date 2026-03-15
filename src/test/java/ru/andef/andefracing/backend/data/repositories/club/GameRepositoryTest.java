package ru.andef.andefracing.backend.data.repositories.club;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:scripts/db/truncate-all-tables-for-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GameRepositoryTest {
    private final GameRepository gameRepository;
    private final ClubRepository clubRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    @Autowired
    public GameRepositoryTest(
            GameRepository gameRepository,
            ClubRepository clubRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository
    ) {
        this.gameRepository = gameRepository;
        this.clubRepository = clubRepository;
        this.regionRepository = regionRepository;
        this.cityRepository = cityRepository;
    }

    private Region createRegion() {
        Region region = new Region((short) 0, "Region", new ArrayList<>());
        return regionRepository.save(region);
    }

    private City createCity(Region region) {
        City city = new City((short) 0, region, "City");
        return cityRepository.save(city);
    }

    private Club createClub(City city) {
        Club club = new Club(
                0,
                city,
                "Club",
                "+7-000-000-00-00",
                "test@example.com",
                "Test address",
                (short) 10,
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        return clubRepository.save(club);
    }

    @Test
    void findAllActiveGamesInClubReturnsOnlyActiveGamesForClub() {
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city);

        Game activeGame1 = new Game((short) 0, "Active1", "url1", true);
        Game activeGame2 = new Game((short) 0, "Active2", "url2", true);
        Game inactiveGame = new Game((short) 0, "Inactive", "url3", false);

        activeGame1 = gameRepository.save(activeGame1);
        activeGame2 = gameRepository.save(activeGame2);
        inactiveGame = gameRepository.save(inactiveGame);

        club.addGame(activeGame1);
        club.addGame(activeGame2);
        club.addGame(inactiveGame);
        clubRepository.save(club);

        List<Game> result = gameRepository.findAllActiveGamesInClub(club.getId());

        assertEquals(2, result.size());
        List<Short> ids = result.stream().map(Game::getId).toList();
        assertTrue(ids.contains(activeGame1.getId()));
        assertTrue(ids.contains(activeGame2.getId()));
    }
}

