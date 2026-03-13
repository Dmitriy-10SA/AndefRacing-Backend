package ru.andef.andefracing.backend.domain.services.search;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.club.*;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.club.*;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.BlockedException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
@Transactional
class ClubSearchServiceTest {
    private final ClubSearchService clubSearchService;
    private final ClubRepository clubRepository;
    private final PhotoRepository photoRepository;
    private final PriceRepository priceRepository;
    private final GameRepository gameRepository;
    private final EmployeeRepository employeeRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    @Autowired
    public ClubSearchServiceTest(
            ClubSearchService clubSearchService,
            ClubRepository clubRepository,
            PhotoRepository photoRepository,
            PriceRepository priceRepository,
            GameRepository gameRepository,
            EmployeeRepository employeeRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository
    ) {
        this.clubSearchService = clubSearchService;
        this.clubRepository = clubRepository;
        this.photoRepository = photoRepository;
        this.priceRepository = priceRepository;
        this.gameRepository = gameRepository;
        this.employeeRepository = employeeRepository;
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

    private Club createClub(City city, String name, boolean isOpen) {
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
        return clubRepository.save(club);
    }

    private Photo createPhoto(String name, short priority) {
        Photo photo = new Photo(name, priority);
        return photoRepository.save(photo);
    }

    private Game createGame(short id, String name) {
        Game game = new Game(id, name, "url", true);
        return gameRepository.save(game);
    }

    private Price createPrice(int value) {
        Price price = new Price(value, (short) 15, BigDecimal.TEN);
        return priceRepository.save(price);
    }

    private Employee createEmployee(String phone, boolean blocked) {
        Employee employee = new Employee("Surname", "Name", "Patronymic", phone);
        employee.setBlocked(blocked);
        return employeeRepository.save(employee);
    }

    @Test
    void findClubByIdReturnsClubWhenExists() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club", true);

        // Act
        Club found = clubSearchService.findClubById(club.getId());

        // Assert
        assertNotNull(found);
        assertEquals(club.getId(), found.getId());
        assertEquals(club.getName(), found.getName());
    }

    @Test
    void findClubByIdThrowsEntityNotFoundExceptionWhenClubDoesNotExist() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findClubById(999)
        );
    }

    @Test
    void findClubByIdReturnsCorrectClubAmongMultiple() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club1 = createClub(city, "Club 1", true);
        Club club2 = createClub(city, "Club 2", true);
        Club club3 = createClub(city, "Club 3", true);

        // Act
        Club found = clubSearchService.findClubById(club2.getId());

        // Assert
        assertEquals(club2.getId(), found.getId());
        assertEquals("Club 2", found.getName());
    }

    @Test
    void findPhotoByIdReturnsPhotoWhenExists() {
        // Arrange
        Photo photo = createPhoto("Test Photo", (short) 1);

        // Act
        Photo found = clubSearchService.findPhotoById(photo.getId());

        // Assert
        assertNotNull(found);
        assertEquals(photo.getId(), found.getId());
        assertEquals(photo.getUrl(), found.getUrl());
    }

    @Test
    void findPhotoByIdThrowsEntityNotFoundExceptionWhenPhotoDoesNotExist() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findPhotoById(999L)
        );
    }

    @Test
    void findPriceByIdReturnsPriceWhenExists() {
        // Arrange
        Price price = createPrice(1000);

        // Act
        Price found = clubSearchService.findPriceById(price.getId());

        // Assert
        assertNotNull(found);
        assertEquals(price.getId(), found.getId());
        assertEquals(1000, found.getValue());
    }

    @Test
    void findPriceByIdThrowsEntityNotFoundExceptionWhenPriceDoesNotExist() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findPriceById(999L)
        );
    }

    @Test
    void findGameByIdReturnsGameWhenExists() {
        // Arrange
        Game game = createGame((short) 1, "Billiards");

        // Act
        Game found = clubSearchService.findGameById((short) 1);

        // Assert
        assertNotNull(found);
        assertEquals(game.getId(), found.getId());
        assertEquals("Billiards", found.getName());
    }

    @Test
    void findGameByIdThrowsEntityNotFoundExceptionWhenGameDoesNotExist() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findGameById((short) 99)
        );
    }

    @Test
    void findEmployeeByPhoneReturnsEmployeeWhenExists() {
        // Arrange
        Employee employee = createEmployee("+7-123-456-78-90", false);

        // Act
        Employee found = clubSearchService.findEmployeeByPhone(employee.getPhone());

        // Assert
        assertNotNull(found);
        assertEquals(employee.getId(), found.getId());
        assertEquals(employee.getPhone(), found.getPhone());
    }

    @Test
    void findEmployeeByPhoneThrowsEntityNotFoundExceptionWhenEmployeeDoesNotExist() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findEmployeeByPhone("+7-000-000-00-00")
        );
    }

    @Test
    void findEmployeeByPhoneThrowsBlockedExceptionWhenEmployeeIsBlocked() {
        // Arrange
        Employee employee = createEmployee("+7-111-111-11-11", true);

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clubSearchService.findEmployeeByPhone(employee.getPhone())
        );
    }

    @Test
    void findEmployeeByPhoneWithCustomExceptionReturnsEmployeeWhenExists() {
        // Arrange
        Employee employee = createEmployee("+7-222-222-22-22", false);
        RuntimeException customException = new RuntimeException("Custom error");

        // Act
        Employee found = clubSearchService.findEmployeeByPhone(employee.getPhone(), customException);

        // Assert
        assertNotNull(found);
        assertEquals(employee.getId(), found.getId());
    }

    @Test
    void findEmployeeByPhoneWithCustomExceptionThrowsCustomExceptionWhenEmployeeDoesNotExist() {
        // Arrange
        RuntimeException customException = new RuntimeException("Custom error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                clubSearchService.findEmployeeByPhone("+7-999-999-99-99", customException)
        );
        assertEquals("Custom error", thrown.getMessage());
    }

    @Test
    void findEmployeeByPhoneWithCustomExceptionThrowsBlockedExceptionWhenEmployeeIsBlocked() {
        // Arrange
        Employee employee = createEmployee("+7-333-333-33-33", true);
        RuntimeException customException = new RuntimeException("Custom error");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clubSearchService.findEmployeeByPhone(employee.getPhone(), customException)
        );
    }

    @Test
    void findEmployeeByIdReturnsEmployeeWhenExists() {
        // Arrange
        Employee employee = createEmployee("+7-444-444-44-44", false);

        // Act
        Employee found = clubSearchService.findEmployeeById(employee.getId());

        // Assert
        assertNotNull(found);
        assertEquals(employee.getId(), found.getId());
        assertEquals(employee.getPhone(), found.getPhone());
    }

    @Test
    void findEmployeeByIdThrowsEntityNotFoundExceptionWhenEmployeeDoesNotExist() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findEmployeeById(999L)
        );
    }

    @Test
    void findEmployeeByIdThrowsBlockedExceptionWhenEmployeeIsBlocked() {
        // Arrange
        Employee employee = createEmployee("+7-555-555-55-55", true);

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clubSearchService.findEmployeeById(employee.getId())
        );
    }

    @Test
    void findEmployeeByIdReturnsCorrectEmployeeAmongMultiple() {
        // Arrange
        Employee employee1 = createEmployee("+7-666-666-66-66", false);
        Employee employee2 = createEmployee("+7-777-777-77-77", false);
        Employee employee3 = createEmployee("+7-888-888-88-88", false);

        // Act
        Employee found = clubSearchService.findEmployeeById(employee2.getId());

        // Assert
        assertEquals(employee2.getId(), found.getId());
        assertEquals("+7-777-777-77-77", found.getPhone());
    }

    @Test
    void getAllOpenClubsInCityReturnsPaginatedList() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        createClub(city, "Club 1", true);
        createClub(city, "Club 2", true);
        createClub(city, "Club 3", true);

        // Act
        PagedClubShortListDto result = clubSearchService.getAllOpenClubsInCity(city.getId(), 0, 2);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(3, result.pageInfoDto().totalElements());
        assertEquals(2, result.pageInfoDto().totalPages());
        assertFalse(result.pageInfoDto().isLast());
    }

    @Test
    void getAllOpenClubsInCityReturnsLastPageCorrectly() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        createClub(city, "Club 1", true);
        createClub(city, "Club 2", true);
        createClub(city, "Club 3", true);

        // Act
        PagedClubShortListDto result = clubSearchService.getAllOpenClubsInCity(city.getId(), 1, 2);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertTrue(result.pageInfoDto().isLast());
    }

    @Test
    void getAllOpenClubsInCityExcludesClosedClubs() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        createClub(city, "Open Club 1", true);
        createClub(city, "Open Club 2", true);
        createClub(city, "Closed Club", false);

        // Act
        PagedClubShortListDto result = clubSearchService.getAllOpenClubsInCity(city.getId(), 0, 10);

        // Assert
        assertEquals(2, result.content().size());
    }

    @Test
    void getAllOpenClubsInCityReturnsEmptyListWhenNoClustersInCity() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);

        // Act
        PagedClubShortListDto result = clubSearchService.getAllOpenClubsInCity(city.getId(), 0, 10);

        // Assert
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pageInfoDto().totalElements());
    }

    @Test
    void getClubFullInfoReturnsCompleteClubInformation() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Full Info Club", true);
        Photo photo = createPhoto("Club Photo", (short) 1);
        club.addPhoto(photo);
        clubRepository.save(club);

        // Act
        ClubFullInfoDto result = clubSearchService.getClubFullInfo(club.getId());

        // Assert
        assertNotNull(result);
        assertEquals(club.getId(), result.getId());
        assertEquals("Full Info Club", result.getName());
    }

    @Test
    void getClubFullInfoThrowsExceptionWhenClubDoesNotExist() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.getClubFullInfo(999)
        );
    }
}