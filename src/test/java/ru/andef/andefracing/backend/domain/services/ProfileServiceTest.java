package ru.andef.andefracing.backend.domain.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.club.EmployeeRepository;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.DuplicateException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.auth.UserNotFoundFromTokenException;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientChangePersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.PagedFavoriteClubShortListDto;
import ru.andef.andefracing.backend.network.dtos.profile.employee.EmployeePersonalInfoDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:scripts/db/truncate-all-tables-for-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileServiceTest {
    private final ProfileService profileService;
    private final ClientRepository clientRepository;
    private final ClubRepository clubRepository;
    private final EmployeeRepository employeeRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    @Autowired
    public ProfileServiceTest(
            ProfileService profileService,
            ClientRepository clientRepository,
            ClubRepository clubRepository,
            EmployeeRepository employeeRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository
    ) {
        this.profileService = profileService;
        this.clientRepository = clientRepository;
        this.clubRepository = clubRepository;
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

    private Client createClient() {
        Client client = new Client("Client", "+7-111-111-11-11", "password");
        return clientRepository.save(client);
    }

    private Employee createEmployee() {
        Employee employee = new Employee("Surname", "Name", "Patronymic", "+7-222-222-22-22");
        employee.setPassword("password");
        return employeeRepository.save(employee);
    }

    @Test
    void getClientPersonalInfoReturnsCorrectClientInfo() {
        // Arrange
        Client client = createClient();

        // Act
        ClientPersonalInfoDto result = profileService.getClientPersonalInfo(client.getId());

        // Assert
        assertNotNull(result);
        assertEquals(client.getName(), result.getName());
        assertEquals(client.getPhone(), result.getPhone());
    }

    @Test
    void getClientPersonalInfoThrowsExceptionWhenClientNotFound() {
        // Arrange
        long nonExistentClientId = 999L;

        // Act & Assert
        assertThrows(UserNotFoundFromTokenException.class, () ->
                profileService.getClientPersonalInfo(nonExistentClientId)
        );
    }

    @Test
    void changeClientPersonalInfoUpdatesClientData() {
        // Arrange
        Client client = createClient();
        ClientChangePersonalInfoDto changeDto = new ClientChangePersonalInfoDto(
                "New Name",
                "+7-999-999-99-99"
        );

        // Act
        profileService.changeClientPersonalInfo(client.getId(), changeDto);

        // Assert
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        assertEquals("New Name", updatedClient.getName());
        assertEquals("+7-999-999-99-99", updatedClient.getPhone());
    }

    @Test
    void addClubToClientFavoriteClubsAddsClubSuccessfully() {
        // Arrange
        Client client = createClient();
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        // Act
        profileService.addClubToClientFavoriteClubs(client.getId(), club.getId());

        // Assert
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        assertTrue(updatedClient.getFavoriteClubs().contains(club));
    }

    @Test
    void addClubToClientFavoriteClubsThrowsExceptionWhenClubAlreadyInFavorites() {
        // Arrange
        Client client = createClient();
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        client.addFavoriteClub(club);
        clientRepository.save(client);

        // Act & Assert
        assertThrows(DuplicateException.class, () ->
                profileService.addClubToClientFavoriteClubs(client.getId(), club.getId())
        );
    }

    @Test
    void getClientFavoriteClubsReturnsPagedList() {
        // Arrange
        Client client = createClient();
        Region region = createRegion();
        regionRepository.save(region);
        City city = createCity(region);
        cityRepository.save(city);
        Club club1 = createClub(city, "Club A");
        club1.addPhoto(new Photo("Photo A", (short) 1));
        Club club2 = createClub(city, "Club B");
        club2.addPhoto(new Photo("Photo B", (short) 2));
        Club club3 = createClub(city, "Club C");
        club3.addPhoto(new Photo("Photo C", (short) 3));
        clubRepository.save(club1);
        clubRepository.save(club2);
        clubRepository.save(club3);

        client.addFavoriteClub(club1);
        client.addFavoriteClub(club2);
        client.addFavoriteClub(club3);
        clientRepository.save(client);

        // Act
        PagedFavoriteClubShortListDto result = profileService.getClientFavoriteClubs(
                client.getId(),
                0,
                2
        );

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(3, result.pageInfo().totalElements());
        assertEquals(2, result.pageInfo().totalPages());
        assertFalse(result.pageInfo().isLast());
    }

    @Test
    void getClientFavoriteClubsReturnsEmptyListWhenNoFavorites() {
        // Arrange
        Client client = createClient();

        // Act
        PagedFavoriteClubShortListDto result = profileService.getClientFavoriteClubs(
                client.getId(),
                0,
                10
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.pageInfo().totalElements());
    }

    @Test
    void deleteClubFromClientFavoriteClubsRemovesClubSuccessfully() {
        // Arrange
        Client client = createClient();
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        client.addFavoriteClub(club);
        clientRepository.save(client);

        // Act
        profileService.deleteClubFromClientFavoriteClubs(client.getId(), club.getId());

        // Assert
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        assertFalse(updatedClient.getFavoriteClubs().contains(club));
    }

    @Test
    void deleteClubFromClientFavoriteClubsThrowsExceptionWhenClubNotInFavorites() {
        // Arrange
        Client client = createClient();
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                profileService.deleteClubFromClientFavoriteClubs(client.getId(), club.getId())
        );
    }

    @Test
    void getEmployeePersonalInfoReturnsCorrectEmployeeInfoWithRoles() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee();

        club.addEmployee(employee, List.of(EmployeeRole.ADMIN, EmployeeRole.EMPLOYEE));
        clubRepository.save(club);

        // Act
        EmployeePersonalInfoDto result = profileService.getEmployeePersonalInfo(
                employee.getId(),
                club.getId()
        );

        // Assert
        assertNotNull(result);
        assertEquals(employee.getName(), result.getName());
        assertEquals(employee.getPhone(), result.getPhone());
        assertEquals(employee.getSurname(), result.getSurname());
        assertEquals(employee.getPatronymic(), result.getPatronymic());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains(EmployeeRole.ADMIN));
        assertTrue(result.getRoles().contains(EmployeeRole.EMPLOYEE));
    }

    @Test
    void getEmployeePersonalInfoReturnsEmptyRolesWhenEmployeeNotInClub() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee();

        // Act
        EmployeePersonalInfoDto result = profileService.getEmployeePersonalInfo(
                employee.getId(),
                club.getId()
        );

        // Assert
        assertNotNull(result);
        assertEquals(employee.getName(), result.getName());
        assertTrue(result.getRoles().isEmpty());
    }

    @Test
    void getEmployeePersonalInfoThrowsExceptionWhenEmployeeNotFound() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        long nonExistentEmployeeId = 999L;

        // Act & Assert
        assertThrows(UserNotFoundFromTokenException.class, () ->
                profileService.getEmployeePersonalInfo(nonExistentEmployeeId, club.getId())
        );
    }
}