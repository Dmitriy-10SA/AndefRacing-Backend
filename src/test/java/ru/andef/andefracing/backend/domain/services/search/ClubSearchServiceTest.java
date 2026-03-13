package ru.andef.andefracing.backend.domain.services.search;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.club.EmployeeRepository;
import ru.andef.andefracing.backend.data.repositories.club.GameRepository;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.BlockedException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
@Transactional
class ClubSearchServiceTest {
    private final ClubSearchService clubSearchService;
    private final ClubRepository clubRepository;
    private final GameRepository gameRepository;
    private final EmployeeRepository employeeRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    @Autowired
    public ClubSearchServiceTest(
            ClubSearchService clubSearchService,
            ClubRepository clubRepository,
            GameRepository gameRepository,
            EmployeeRepository employeeRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository
    ) {
        this.clubSearchService = clubSearchService;
        this.clubRepository = clubRepository;
        this.gameRepository = gameRepository;
        this.employeeRepository = employeeRepository;
        this.regionRepository = regionRepository;
        this.cityRepository = cityRepository;
    }

    private Region createRegion() {
        Region region = new Region((short) 0, "Test Region", new ArrayList<>());
        return regionRepository.save(region);
    }

    private City createCity(Region region) {
        City city = new City((short) 0, region, "Test City");
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

    private Employee createEmployee(String phone) {
        Employee employee = new Employee("Surname", "Name", "Patronymic", phone);
        return employeeRepository.save(employee);
    }

    private Employee createBlockedEmployee(String phone) {
        Employee employee = new Employee("Surname", "Name", "Patronymic", phone);
        employee.setBlocked(true);
        return employeeRepository.save(employee);
    }

    private void createPhoto(Club club, String url, short order) {
        Photo photo = new Photo(url, order);
        club.addPhoto(photo);
        clubRepository.save(club);
    }

    private void createPrice(Club club, BigDecimal price) {
        Price priceEntity = new Price((short) 60, price);
        club.addPrice(priceEntity);
        clubRepository.save(club);
    }

    private Game createGame() {
        Game game = new Game((short) 0, "Test Game", "Description", true);
        return gameRepository.save(game);
    }

    @Test
    void findPhotoByIdReturnsPhotoWhenExists() {
        // Arrange
        Region region = createRegion();
        regionRepository.save(region);
        City city = createCity(region);
        cityRepository.save(city);
        Club club = createClub(city, "Test Club", true);
        club.addPhoto(new Photo("http://example.com/photo.jpg", (short) 1));
        clubRepository.save(club);

        // Act
        Photo result = clubSearchService.findPhotoById(club.getPhotos().get(0).getId());

        // Assert
        assertNotNull(result);
        assertEquals(club.getPhotos().get(0).getId(), result.getId());
        assertEquals("http://example.com/photo.jpg", result.getUrl());
    }

    @Test
    void findPhotoByIdThrowsExceptionWhenPhotoNotFound() {
        // Arrange
        long nonExistentId = 999L;

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findPhotoById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    void findPriceByIdReturnsPriceWhenExists() {
        // Arrange
        Region region = createRegion();
        regionRepository.save(region);
        City city = createCity(region);
        cityRepository.save(city);
        Club club = createClub(city, "Test Club", true);
        club.addPrice(new Price((short) 60, new BigDecimal("1000.00")));
        clubRepository.save(club);

        // Act
        Price result = clubSearchService.findPriceById(club.getPrices().get(0).getId());

        // Assert
        assertNotNull(result);
        assertEquals(club.getPrices().get(0).getId(), result.getId());
        assertEquals(new BigDecimal("1000.00"), result.getValue());
    }

    @Test
    void findPriceByIdThrowsExceptionWhenPriceNotFound() {
        // Arrange
        long nonExistentId = 999L;

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findPriceById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    void findGameByIdReturnsGameWhenExists() {
        // Arrange
        Game game = createGame();

        // Act
        Game result = clubSearchService.findGameById(game.getId());

        // Assert
        assertNotNull(result);
        assertEquals(game.getId(), result.getId());
        assertEquals(game.getName(), result.getName());
    }

    @Test
    void findGameByIdThrowsExceptionWhenGameNotFound() {
        // Arrange
        short nonExistentId = 999;

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findGameById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    void findEmployeeByPhoneReturnsEmployeeWhenExists() {
        // Arrange
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act
        Employee result = clubSearchService.findEmployeeByPhone("+7-111-111-11-11");

        // Assert
        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
        assertEquals(employee.getPhone(), result.getPhone());
    }

    @Test
    void findEmployeeByPhoneThrowsExceptionWhenEmployeeNotFound() {
        // Arrange
        String nonExistentPhone = "+7-999-999-99-99";

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findEmployeeByPhone(nonExistentPhone)
        );
        assertTrue(exception.getMessage().contains(nonExistentPhone));
    }

    @Test
    void findEmployeeByPhoneThrowsExceptionWhenEmployeeIsBlocked() {
        // Arrange
        Employee blockedEmployee = createBlockedEmployee("+7-222-222-22-22");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clubSearchService.findEmployeeByPhone(blockedEmployee.getPhone())
        );
    }

    @Test
    void findEmployeeByPhoneWithCustomExceptionThrowsCustomException() {
        // Arrange
        String nonExistentPhone = "+7-999-999-99-99";
        RuntimeException customException = new IllegalArgumentException("Custom error message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                clubSearchService.findEmployeeByPhone(nonExistentPhone, customException)
        );
        assertEquals("Custom error message", exception.getMessage());
    }

    @Test
    void findEmployeeByPhoneWithCustomExceptionReturnsEmployeeWhenExists() {
        // Arrange
        Employee employee = createEmployee("+7-333-333-33-33");
        RuntimeException customException = new IllegalArgumentException("Should not be thrown");

        // Act
        Employee result = clubSearchService.findEmployeeByPhone("+7-333-333-33-33", customException);

        // Assert
        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
    }

    @Test
    void findEmployeeByPhoneWithCustomExceptionThrowsBlockedExceptionWhenEmployeeIsBlocked() {
        // Arrange
        Employee blockedEmployee = createBlockedEmployee("+7-444-444-44-44");
        RuntimeException customException = new IllegalArgumentException("Custom error");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clubSearchService.findEmployeeByPhone(blockedEmployee.getPhone(), customException)
        );
    }

    @Test
    void findEmployeeByIdReturnsEmployeeWhenExists() {
        // Arrange
        Employee employee = createEmployee("+7-555-555-55-55");

        // Act
        Employee result = clubSearchService.findEmployeeById(employee.getId());

        // Assert
        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
        assertEquals(employee.getPhone(), result.getPhone());
    }

    @Test
    void findEmployeeByIdThrowsExceptionWhenEmployeeNotFound() {
        // Arrange
        long nonExistentId = 999L;

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findEmployeeById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    void findEmployeeByIdThrowsExceptionWhenEmployeeIsBlocked() {
        // Arrange
        Employee blockedEmployee = createBlockedEmployee("+7-666-666-66-66");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clubSearchService.findEmployeeById(blockedEmployee.getId())
        );
    }

    @Test
    void findClubByIdReturnsClubWhenExists() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club", true);

        // Act
        Club result = clubSearchService.findClubById(club.getId());

        // Assert
        assertNotNull(result);
        assertEquals(club.getId(), result.getId());
        assertEquals(club.getName(), result.getName());
    }

    @Test
    void findClubByIdThrowsExceptionWhenClubNotFound() {
        // Arrange
        int nonExistentId = 999;

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.findClubById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    void getAllOpenClubsInCityReturnsOnlyOpenClubs() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club openClub1 = createClub(city, "Open Club 1", true);
        Club openClub2 = createClub(city, "Open Club 2", true);

        createPhoto(openClub1, "http://example.com/photo1.jpg", (short) 1);
        createPhoto(openClub2, "http://example.com/photo2.jpg", (short) 1);

        // Act
        PagedClubShortListDto result = clubSearchService.getAllOpenClubsInCity(city.getId(), 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(2, result.pageInfoDto().totalElements());
        assertEquals(1, result.pageInfoDto().totalPages());
        assertTrue(result.pageInfoDto().isLast());
    }

    @Test
    void getAllOpenClubsInCityReturnsPaginatedResults() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);

        for (int i = 1; i <= 5; i++) {
            Club club = createClub(city, "Club " + i, true);
            createPhoto(club, "http://example.com/photo" + i + ".jpg", (short) 1);
        }

        // Act
        PagedClubShortListDto result = clubSearchService.getAllOpenClubsInCity(city.getId(), 0, 2);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(5, result.pageInfoDto().totalElements());
        assertEquals(3, result.pageInfoDto().totalPages());
        assertFalse(result.pageInfoDto().isLast());
    }

    @Test
    void getAllOpenClubsInCityReturnsEmptyListWhenNoOpenClubs() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        createClub(city, "Closed Club", false);

        // Act
        PagedClubShortListDto result = clubSearchService.getAllOpenClubsInCity(city.getId(), 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pageInfoDto().totalElements());
    }

    @Test
    void getAllOpenClubsInCityThrowsExceptionWhenCityNotFound() {
        // Arrange
        short nonExistentCityId = 999;

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.getAllOpenClubsInCity(nonExistentCityId, 0, 10)
        );
    }

    @Test
    void getClubFullInfoReturnsCompleteClubInformation() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club", true);

        createPhoto(club, "http://example.com/photo1.jpg", (short) 1);
        createPhoto(club, "http://example.com/photo2.jpg", (short) 2);
        createPrice(club, new BigDecimal("1000.00"));

        Game game = createGame();
        club.addGame(game);
        clubRepository.save(club);

        // Act
        ClubFullInfoDto result = clubSearchService.getClubFullInfo(club.getId());

        // Assert
        assertNotNull(result);
        assertEquals(club.getId(), result.getId());
        assertEquals(club.getName(), result.getName());
        assertEquals(club.getPhone(), result.getPhone());
        assertEquals(club.getEmail(), result.getEmail());
        assertEquals(club.getAddress(), result.getAddress());
        assertNotNull(result.getPhotos());
        assertNotNull(result.getPrices());
        assertNotNull(result.getGames());
    }

    @Test
    void getClubFullInfoThrowsExceptionWhenClubNotFound() {
        // Arrange
        int nonExistentId = 999;

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubSearchService.getClubFullInfo(nonExistentId)
        );
    }
}