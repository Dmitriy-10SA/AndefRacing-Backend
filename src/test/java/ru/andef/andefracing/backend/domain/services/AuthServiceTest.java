package ru.andef.andefracing.backend.domain.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.club.EmployeeRepository;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.BlockedException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.auth.ClientWithThisPhoneAlreadyExistsException;
import ru.andef.andefracing.backend.domain.exceptions.auth.InvalidPhoneOrPasswordException;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientLoginDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeLoginDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
@Transactional
class AuthServiceTest {
    private final AuthService authService;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ClubRepository clubRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceTest(
            AuthService authService,
            ClientRepository clientRepository,
            EmployeeRepository employeeRepository,
            ClubRepository clubRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authService = authService;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.clubRepository = clubRepository;
        this.regionRepository = regionRepository;
        this.cityRepository = cityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Region createRegion() {
        Region region = new Region((short) 0, "Test Region", new ArrayList<>());
        return regionRepository.save(region);
    }

    private City createCity(Region region) {
        City city = new City((short) 0, region, "Test City");
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

    private Client createClient(String name, String phone, String password) {
        String passwordHash = passwordEncoder.encode(password);
        Client client = new Client(name, phone, passwordHash);
        return clientRepository.save(client);
    }

    private Client createBlockedClient(String name, String phone, String password) {
        String passwordHash = passwordEncoder.encode(password);
        Client client = new Client(name, phone, passwordHash);
        client.setBlocked(true);
        return clientRepository.save(client);
    }

    private Employee createEmployee(String phone) {
        Employee employee = new Employee("Surname", "Name", "Patronymic", phone);
        return employeeRepository.save(employee);
    }

    private Employee createEmployeeWithPassword(String phone, String password) {
        Employee employee = new Employee("Surname", "Name", "Patronymic", phone);
        String passwordHash = passwordEncoder.encode(password);
        employee.setPassword(passwordHash);
        return employeeRepository.save(employee);
    }

    private Employee createBlockedEmployee(String phone) {
        Employee employee = new Employee("Surname", "Name", "Patronymic", phone);
        employee.setBlocked(true);
        return employeeRepository.save(employee);
    }

    @Test
    void registerClientCreatesNewClientAndReturnsJwt() {
        // Arrange
        ClientRegisterDto registerDto = new ClientRegisterDto(
                "Test Client",
                "+7-111-111-11-11",
                "password123"
        );

        // Act
        ClientAuthResponseDto result = authService.registerClient(registerDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getJwt());
        assertFalse(result.getJwt().isEmpty());

        // Verify client was created
        Client client = clientRepository.findByPhone("+7-111-111-11-11").orElseThrow();
        assertEquals("Test Client", client.getName());
        assertTrue(passwordEncoder.matches("password123", client.getPassword()));
    }

    @Test
    void registerClientThrowsExceptionWhenPhoneAlreadyExists() {
        // Arrange
        createClient("Existing Client", "+7-111-111-11-11", "password");
        ClientRegisterDto registerDto = new ClientRegisterDto(
                "New Client",
                "+7-111-111-11-11",
                "password123"
        );

        // Act & Assert
        assertThrows(ClientWithThisPhoneAlreadyExistsException.class, () ->
                authService.registerClient(registerDto)
        );
    }

    @Test
    void loginClientReturnsJwtWhenCredentialsAreValid() {
        // Arrange
        createClient("Test Client", "+7-111-111-11-11", "password123");
        ClientLoginDto loginDto = new ClientLoginDto("+7-111-111-11-11", "password123");

        // Act
        ClientAuthResponseDto result = authService.loginClient(loginDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getJwt());
        assertFalse(result.getJwt().isEmpty());
    }

    @Test
    void loginClientThrowsExceptionWhenPhoneNotFound() {
        // Arrange
        ClientLoginDto loginDto = new ClientLoginDto("+7-999-999-99-99", "password123");

        // Act & Assert
        assertThrows(InvalidPhoneOrPasswordException.class, () ->
                authService.loginClient(loginDto)
        );
    }

    @Test
    void loginClientThrowsExceptionWhenPasswordIsInvalid() {
        // Arrange
        createClient("Test Client", "+7-111-111-11-11", "password123");
        ClientLoginDto loginDto = new ClientLoginDto("+7-111-111-11-11", "wrongpassword");

        // Act & Assert
        assertThrows(InvalidPhoneOrPasswordException.class, () ->
                authService.loginClient(loginDto)
        );
    }

    @Test
    void loginClientThrowsExceptionWhenClientIsBlocked() {
        // Arrange
        createBlockedClient("Blocked Client", "+7-111-111-11-11", "password123");
        ClientLoginDto loginDto = new ClientLoginDto("+7-111-111-11-11", "password123");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                authService.loginClient(loginDto)
        );
    }

    @Test
    void changeClientPasswordUpdatesPasswordAndReturnsJwt() {
        // Arrange
        Client client = createClient("Test Client", "+7-111-111-11-11", "oldpassword");
        ClientChangePasswordDto changePasswordDto = new ClientChangePasswordDto(
                "+7-111-111-11-11",
                "newpassword123"
        );

        // Act
        ClientAuthResponseDto result = authService.changeClientPassword(changePasswordDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getJwt());
        assertFalse(result.getJwt().isEmpty());

        // Verify password was changed
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newpassword123", updatedClient.getPassword()));
        assertFalse(passwordEncoder.matches("oldpassword", updatedClient.getPassword()));
    }

    @Test
    void changeClientPasswordThrowsExceptionWhenClientNotFound() {
        // Arrange
        ClientChangePasswordDto changePasswordDto = new ClientChangePasswordDto(
                "+7-999-999-99-99",
                "newpassword123"
        );

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                authService.changeClientPassword(changePasswordDto)
        );
    }

    @Test
    void isEmployeeFirstEnterReturnsTrueWhenEmployeeNeedsPassword() {
        // Arrange
        Employee employee = createEmployee("+7-222-222-22-22");

        // Act
        boolean result = authService.isEmployeeFirstEnter("+7-222-222-22-22");

        // Assert
        assertTrue(result);
        assertTrue(employee.isNeedPassword());
    }

    @Test
    void isEmployeeFirstEnterReturnsFalseWhenEmployeeHasPassword() {
        // Arrange
        Employee employee = createEmployeeWithPassword("+7-222-222-22-22", "password123");

        // Act
        boolean result = authService.isEmployeeFirstEnter("+7-222-222-22-22");

        // Assert
        assertFalse(result);
        assertFalse(employee.isNeedPassword());
    }

    @Test
    void isEmployeeFirstEnterThrowsExceptionWhenEmployeeNotFound() {
        // Arrange
        String nonExistentPhone = "+7-999-999-99-99";

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                authService.isEmployeeFirstEnter(nonExistentPhone)
        );
    }

    @Test
    void preLoginEmployeeSetsPasswordOnFirstLoginAndReturnsClubs() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club1 = createClub(city, "Club 1");
        Club club2 = createClub(city, "Club 2");

        Employee employee = createEmployee("+7-222-222-22-22");
        club1.addEmployee(employee, List.of(EmployeeRole.ADMIN));
        club2.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE));
        clubRepository.save(club1);
        clubRepository.save(club2);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "newpassword123");

        // Act
        List<EmployeeClubDto> result = authService.preLoginEmployee(loginDto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify password was set
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
        assertFalse(updatedEmployee.isNeedPassword());
        assertTrue(passwordEncoder.matches("newpassword123", updatedEmployee.getPassword()));
    }

    @Test
    void preLoginEmployeeValidatesPasswordWhenNotFirstLoginAndReturnsClubs() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        Employee employee = createEmployeeWithPassword("+7-222-222-22-22", "password123");
        club.addEmployee(employee, List.of(EmployeeRole.ADMIN));
        clubRepository.save(club);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "password123");

        // Act
        List<EmployeeClubDto> result = authService.preLoginEmployee(loginDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(club.getId(), result.get(0).getId());
        assertEquals(club.getName(), result.get(0).getName());
    }

    @Test
    void preLoginEmployeeThrowsExceptionWhenPasswordIsInvalidOnNotFirstLogin() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        Employee employee = createEmployeeWithPassword("+7-222-222-22-22", "password123");
        club.addEmployee(employee, List.of(EmployeeRole.ADMIN));
        clubRepository.save(club);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "wrongpassword");

        // Act & Assert
        assertThrows(InvalidPhoneOrPasswordException.class, () ->
                authService.preLoginEmployee(loginDto)
        );
    }

    @Test
    void preLoginEmployeeThrowsExceptionWhenEmployeeNotFound() {
        // Arrange
        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123");

        // Act & Assert
        assertThrows(InvalidPhoneOrPasswordException.class, () ->
                authService.preLoginEmployee(loginDto)
        );
    }

    @Test
    void preLoginEmployeeThrowsExceptionWhenEmployeeIsBlocked() {
        // Arrange
        createBlockedEmployee("+7-222-222-22-22");
        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "password123");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                authService.preLoginEmployee(loginDto)
        );
    }

    @Test
    void loginEmployeeReturnsJwtWithRolesWhenCredentialsAreValid() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        Employee employee = createEmployeeWithPassword("+7-222-222-22-22", "password123");
        club.addEmployee(employee, List.of(EmployeeRole.ADMIN, EmployeeRole.EMPLOYEE));
        clubRepository.save(club);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "password123");

        // Act
        EmployeeAuthResponseDto result = authService.loginEmployee(club.getId(), loginDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getJwt());
        assertFalse(result.getJwt().isEmpty());
    }

    @Test
    void loginEmployeeThrowsExceptionWhenEmployeeNotFound() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123");

        // Act & Assert
        assertThrows(InvalidPhoneOrPasswordException.class, () ->
                authService.loginEmployee(club.getId(), loginDto)
        );
    }

    @Test
    void loginEmployeeThrowsExceptionWhenPasswordIsInvalid() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        Employee employee = createEmployeeWithPassword("+7-222-222-22-22", "password123");
        club.addEmployee(employee, List.of(EmployeeRole.ADMIN));
        clubRepository.save(club);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "wrongpassword");

        // Act & Assert
        assertThrows(InvalidPhoneOrPasswordException.class, () ->
                authService.loginEmployee(club.getId(), loginDto)
        );
    }

    @Test
    void loginEmployeeThrowsExceptionWhenClubNotFound() {
        // Arrange
        Employee employee = createEmployeeWithPassword("+7-222-222-22-22", "password123");
        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "password123");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                authService.loginEmployee(999, loginDto)
        );
    }

    @Test
    void loginEmployeeThrowsExceptionWhenEmployeeNotInClub() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        Employee employee = createEmployeeWithPassword("+7-222-222-22-22", "password123");
        // Employee не добавлен в клуб

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "password123");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                authService.loginEmployee(club.getId(), loginDto)
        );
    }

    @Test
    void loginEmployeeThrowsExceptionWhenEmployeeIsBlocked() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        Employee employee = createBlockedEmployee("+7-222-222-22-22");
        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-222-222-22-22", "password123");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                authService.loginEmployee(club.getId(), loginDto)
        );
    }
}