package ru.andef.andefracing.backend.data.repositories.club;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:scripts/db/truncate-all-tables-for-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WorkScheduleExceptionRepositoryTest {
    private final WorkScheduleExceptionRepository workScheduleExceptionRepository;
    private final ClubRepository clubRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    @Autowired
    public WorkScheduleExceptionRepositoryTest(
            WorkScheduleExceptionRepository workScheduleExceptionRepository,
            ClubRepository clubRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository
    ) {
        this.workScheduleExceptionRepository = workScheduleExceptionRepository;
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

    private Club createClub(City city, String name) {
        Club club = new Club(
                0,
                city,
                name,
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
    void findByClubIdAndDateReturnsSingleExceptionForClubAndDate() {
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Club");

        WorkScheduleException exception = new WorkScheduleException(LocalDate.of(2026, 1, 1), "Holiday");
        club.addWorkScheduleException(exception);
        clubRepository.save(club);

        Optional<WorkScheduleException> result =
                workScheduleExceptionRepository.findByClubIdAndDate(club.getId(), LocalDate.of(2026, 1, 1));

        assertTrue(result.isPresent());
        assertEquals(exception.getDate(), result.get().getDate());
        assertEquals(exception.getDescription(), result.get().getDescription());
    }

    @Test
    void findAllByRangeOfDatesBetweenStartAndEndReturnsAllExceptionsInRangeForClub() {
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Club");

        WorkScheduleException exception1 = new WorkScheduleException(LocalDate.of(2026, 1, 1), "Holiday1");
        WorkScheduleException exception2 = new WorkScheduleException(LocalDate.of(2026, 1, 10), "Holiday2");
        WorkScheduleException exception3 = new WorkScheduleException(LocalDate.of(2026, 2, 1), "Holiday3");

        club.addWorkScheduleException(exception1);
        club.addWorkScheduleException(exception2);
        club.addWorkScheduleException(exception3);
        clubRepository.save(club);

        List<WorkScheduleException> result = workScheduleExceptionRepository
                .findAllByRangeOfDatesBetweenStartAndEnd(
                        club.getId(),
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 1, 31)
                );

        assertEquals(2, result.size());
        List<LocalDate> dates = result.stream().map(WorkScheduleException::getDate).toList();
        assertTrue(dates.contains(LocalDate.of(2026, 1, 1)));
        assertTrue(dates.contains(LocalDate.of(2026, 1, 10)));
        assertFalse(dates.contains(LocalDate.of(2026, 2, 1)));
    }

    @Test
    void findByIdAndClubIdReturnsExceptionOnlyForGivenClub() {
        Region region = createRegion();
        City city = createCity(region);
        Club club1 = createClub(city, "Club1");
        Club club2 = createClub(city, "Club2");

        WorkScheduleException exception1 = new WorkScheduleException(LocalDate.of(2026, 1, 1), "Holiday1");
        WorkScheduleException exception2 = new WorkScheduleException(LocalDate.of(2026, 1, 2), "Holiday2");

        club1.addWorkScheduleException(exception1);
        club2.addWorkScheduleException(exception2);
        clubRepository.save(club1);
        clubRepository.save(club2);

        // Получаем реальные ID исключений из базы
        List<WorkScheduleException> allExceptions = workScheduleExceptionRepository.findAll();
        WorkScheduleException persistedException1 = allExceptions.stream()
                .filter(e -> e.getDate().equals(LocalDate.of(2026, 1, 1)))
                .findFirst()
                .orElseThrow();

        Optional<WorkScheduleException> resultForClub1 =
                workScheduleExceptionRepository.findByIdAndClubId(persistedException1.getId(), club1.getId());
        Optional<WorkScheduleException> resultForClub2WrongClub =
                workScheduleExceptionRepository.findByIdAndClubId(persistedException1.getId(), club2.getId());

        assertTrue(resultForClub1.isPresent());
        assertEquals(persistedException1.getId(), resultForClub1.get().getId());
        assertTrue(resultForClub2WrongClub.isEmpty());
    }
}

