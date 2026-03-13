package ru.andef.andefracing.backend.domain.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.club.EmployeeRepository;
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
import ru.andef.andefracing.backend.network.security.jwt.JwtUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
@Transactional
class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private Client createTestClient(String phone, String password) {
        Client client = new Client("Test Client", phone, passwordEncoder.encode(password));
        return clientRepository.save(client);
    }

    private long createTestRegion() {
        jdbcTemplate.update("INSERT INTO location.region (name) VALUES (?)", "Test Region");
        return jdbcTemplate.queryForObject("SELECT id FROM location.region WHERE name = ?", Long.class, "Test Region");
    }

    private long createTestCity(long regionId) {
        jdbcTemplate.update("INSERT INTO location.city (region_id, name) VALUES (?, ?)", regionId, "Test City");
        return jdbcTemplate.queryForObject("SELECT id FROM location.city WHERE name = ?", Long.class, "Test City");
    }

    private int createTestClub(long cityId) {
        jdbcTemplate.update(
            "INSERT INTO info.club (city_id, name, phone, email, address, cnt_equipment, is_open) VALUES (?, ?, ?, ?, ?, ?, ?)",
            cityId, "Test Club", "+7-999-999-99-99", "test@club.com", "Test Address", (short) 10, true
        );
        return jdbcTemplate.queryForObject("SELECT id FROM info.club WHERE name = ?", Integer.class, "Test Club");
    }

    private long createTestEmployee(String phone, boolean needPassword) {
        String password = needPassword ? null : passwordEncoder.encode("password123!A");
        jdbcTemplate.update(
            "INSERT INTO hr.employee (surname, name, patronymic, phone, password, need_password, is_blocked) VALUES (?, ?, ?, ?, ?, ?, ?)",
            "TestSurname", "TestName", "TestPatronymic", phone, password, needPassword, false
        );
        return jdbcTemplate.queryForObject("SELECT id FROM hr.employee WHERE phone = ?", Long.class, phone);
    }

    private void addEmployeeToClub(int clubId, long employeeId, EmployeeRole role) {
        jdbcTemplate.update(
            "INSERT INTO hr.employee_club (club_id, employee_id, employee_role) VALUES (?, ?, ?)",
            clubId, employeeId, role.name()
        );
    }

    @Test
    void registerClient_shouldSuccess_whenValidData() {
        // Given
        ClientRegisterDto registerDto = new ClientRegisterDto(
                "Test Client",
                "+7-999-999-99-99",
                "Password123!"
        );

        // When
        ClientAuthResponseDto response = authService.registerClient(registerDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getJwt());
        assertFalse(response.getJwt().isEmpty());

        // Verify client was saved
        Client savedClient = clientRepository.findByPhone("+7-999-999-99-99").orElse(null);
        assertNotNull(savedClient);
        assertEquals("Test Client", savedClient.getName());
        assertTrue(passwordEncoder.matches("Password123!", savedClient.getPassword()));
    }

    @Test
    void registerClient_shouldThrowException_whenPhoneAlreadyExists() {
        // Given
        createTestClient("+7-999-999-99-99", "password123!A");

        ClientRegisterDto registerDto = new ClientRegisterDto(
                "Another Client",
                "+7-999-999-99-99",
                "Password123!"
        );

        // When & Then
        ClientWithThisPhoneAlreadyExistsException exception = assertThrows(
                ClientWithThisPhoneAlreadyExistsException.class,
                () -> authService.registerClient(registerDto)
        );
        assertEquals("+7-999-999-99-99", exception.getPhone());
    }

    @Test
    void loginClient_shouldSuccess_whenValidCredentials() {
        // Given
        createTestClient("+7-999-999-99-99", "password123!A");
        ClientLoginDto loginDto = new ClientLoginDto("+7-999-999-99-99", "password123!A");

        // When
        ClientAuthResponseDto response = authService.loginClient(loginDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getJwt());
        assertFalse(response.getJwt().isEmpty());
    }

    @Test
    void loginClient_shouldThrowException_whenInvalidPhone() {
        // Given
        ClientLoginDto loginDto = new ClientLoginDto("+7-999-999-99-99", "password123!A");

        // When & Then
        assertThrows(InvalidPhoneOrPasswordException.class, () -> authService.loginClient(loginDto));
    }

    @Test
    void loginClient_shouldThrowException_whenInvalidPassword() {
        // Given
        createTestClient("+7-999-999-99-99", "password123!A");
        ClientLoginDto loginDto = new ClientLoginDto("+7-999-999-99-99", "wrongPassword");

        // When & Then
        assertThrows(InvalidPhoneOrPasswordException.class, () -> authService.loginClient(loginDto));
    }

    @Test
    void changeClientPassword_shouldSuccess_whenValidPhone() {
        // Given
        createTestClient("+7-999-999-99-99", "oldPassword123!A");
        ClientChangePasswordDto changePasswordDto = new ClientChangePasswordDto(
                "+7-999-999-99-99",
                "NewPassword123!"
        );

        // When
        ClientAuthResponseDto response = authService.changeClientPassword(changePasswordDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getJwt());

        // Verify password was changed
        Client client = clientRepository.findByPhone("+7-999-999-99-99").orElseThrow();
        assertTrue(passwordEncoder.matches("NewPassword123!", client.getPassword()));
    }

    @Test
    void changeClientPassword_shouldThrowException_whenPhoneNotFound() {
        // Given
        ClientChangePasswordDto changePasswordDto = new ClientChangePasswordDto(
                "+7-999-999-99-99",
                "NewPassword123!"
        );

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> authService.changeClientPassword(changePasswordDto));
    }

    @Test
    void isEmployeeFirstEnter_shouldReturnTrue_whenNeedPassword() {
        // Given
        createTestEmployee("+7-999-999-99-99", true);

        // When
        boolean result = authService.isEmployeeFirstEnter("+7-999-999-99-99");

        // Then
        assertTrue(result);
    }

    @Test
    void isEmployeeFirstEnter_shouldReturnFalse_whenPasswordAlreadySet() {
        // Given
        createTestEmployee("+7-999-999-99-99", false);

        // When
        boolean result = authService.isEmployeeFirstEnter("+7-999-999-99-99");

        // Then
        assertFalse(result);
    }

    @Test
    void isEmployeeFirstEnter_shouldThrowException_whenEmployeeNotFound() {
        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> authService.isEmployeeFirstEnter("+7-999-999-99-99"));
    }

    @Test
    void preLoginEmployee_shouldSetPassword_whenFirstEnter() {
        // Given
        long regionId = createTestRegion();
        long cityId = createTestCity(regionId);
        int clubId = createTestClub(cityId);
        long employeeId = createTestEmployee("+7-999-999-99-99", true);

        // Add employee to club
        addEmployeeToClub(clubId, employeeId, EmployeeRole.EMPLOYEE);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "NewPassword123!");

        // When
        List<EmployeeClubDto> clubs = authService.preLoginEmployee(loginDto);

        // Then
        assertNotNull(clubs);
        assertEquals(1, clubs.size());
        assertEquals("Test Club", clubs.get(0).getName());

        // Verify password was set
        Employee updatedEmployee = employeeRepository.findByPhone("+7-999-999-99-99").orElseThrow();
        assertFalse(updatedEmployee.isNeedPassword());
        assertTrue(passwordEncoder.matches("NewPassword123!", updatedEmployee.getPassword()));
    }

    @Test
    void preLoginEmployee_shouldValidatePassword_whenNotFirstEnter() {
        // Given
        long regionId = createTestRegion();
        long cityId = createTestCity(regionId);
        int clubId = createTestClub(cityId);
        long employeeId = createTestEmployee("+7-999-999-99-99", false);

        // Add employee to club
        addEmployeeToClub(clubId, employeeId, EmployeeRole.EMPLOYEE);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123!A");

        // When
        List<EmployeeClubDto> clubs = authService.preLoginEmployee(loginDto);

        // Then
        assertNotNull(clubs);
        assertEquals(1, clubs.size());
    }

    @Test
    void preLoginEmployee_shouldThrowException_whenInvalidPassword() {
        // Given
        long regionId = createTestRegion();
        long cityId = createTestCity(regionId);
        int clubId = createTestClub(cityId);
        long employeeId = createTestEmployee("+7-999-999-99-99", false);

        // Add employee to club
        addEmployeeToClub(clubId, employeeId, EmployeeRole.EMPLOYEE);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "wrongPassword");

        // When & Then
        assertThrows(InvalidPhoneOrPasswordException.class,
                () -> authService.preLoginEmployee(loginDto));
    }

    @Test
    void preLoginEmployee_shouldThrowException_whenEmployeeNotFound() {
        // Given
        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123!A");

        // When & Then
        assertThrows(InvalidPhoneOrPasswordException.class,
                () -> authService.preLoginEmployee(loginDto));
    }

    @Test
    void preLoginEmployee_shouldReturnEmptyList_whenEmployeeHasNoClubs() {
        // Given
        createTestEmployee("+7-999-999-99-99", true);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "NewPassword123!");

        // When
        List<EmployeeClubDto> clubs = authService.preLoginEmployee(loginDto);

        // Then
        assertNotNull(clubs);
        assertTrue(clubs.isEmpty());
    }

    @Test
    void loginEmployee_shouldSuccess_whenValidCredentials() {
        // Given
        long regionId = createTestRegion();
        long cityId = createTestCity(regionId);
        int clubId = createTestClub(cityId);
        long employeeId = createTestEmployee("+7-999-999-99-99", false);

        // Add employee to club with roles
        addEmployeeToClub(clubId, employeeId, EmployeeRole.ADMIN);
        addEmployeeToClub(clubId, employeeId, EmployeeRole.MANAGER);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123!A");

        // When
        EmployeeAuthResponseDto response = authService.loginEmployee(clubId, loginDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getJwt());
        assertFalse(response.getJwt().isEmpty());
    }

    @Test
    void loginEmployee_shouldThrowException_whenInvalidPassword() {
        // Given
        long regionId = createTestRegion();
        long cityId = createTestCity(regionId);
        int clubId = createTestClub(cityId);
        long employeeId = createTestEmployee("+7-999-999-99-99", false);

        addEmployeeToClub(clubId, employeeId, EmployeeRole.EMPLOYEE);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "wrongPassword");

        // When & Then
        assertThrows(InvalidPhoneOrPasswordException.class,
                () -> authService.loginEmployee(clubId, loginDto));
    }

    @Test
    void loginEmployee_shouldThrowException_whenEmployeeNotInClub() {
        // Given
        long regionId = createTestRegion();
        long cityId = createTestCity(regionId);
        int clubId = createTestClub(cityId);
        createTestEmployee("+7-999-999-99-99", false);

        // Do NOT add employee to club

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123!A");

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> authService.loginEmployee(clubId, loginDto));
    }

    @Test
    void loginEmployee_shouldThrowException_whenClubNotFound() {
        // Given
        createTestEmployee("+7-999-999-99-99", false);
        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123!A");

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> authService.loginEmployee(999, loginDto));
    }

    @Test
    void loginEmployee_shouldThrowException_whenEmployeeNotFound() {
        // Given
        long regionId = createTestRegion();
        long cityId = createTestCity(regionId);
        int clubId = createTestClub(cityId);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123!A");

        // When & Then
        assertThrows(InvalidPhoneOrPasswordException.class,
                () -> authService.loginEmployee(clubId, loginDto));
    }

    @Test
    void registerClient_shouldGenerateValidJwtToken() {
        // Given
        ClientRegisterDto registerDto = new ClientRegisterDto(
                "Test Client",
                "+7-999-999-99-99",
                "Password123!"
        );

        // When
        ClientAuthResponseDto response = authService.registerClient(registerDto);

        // Then
        assertDoesNotThrow(() -> jwtUtils.extractClaims(response.getJwt()));
    }

    @Test
    void loginEmployee_shouldGenerateValidJwtTokenWithRoles() {
        // Given
        long regionId = createTestRegion();
        long cityId = createTestCity(regionId);
        int clubId = createTestClub(cityId);
        long employeeId = createTestEmployee("+7-999-999-99-99", false);

        addEmployeeToClub(clubId, employeeId, EmployeeRole.ADMIN);

        EmployeeLoginDto loginDto = new EmployeeLoginDto("+7-999-999-99-99", "password123!A");

        // When
        EmployeeAuthResponseDto response = authService.loginEmployee(clubId, loginDto);

        // Then
        assertDoesNotThrow(() -> {
            var claims = jwtUtils.extractClaims(response.getJwt());
            assertNotNull(claims.get("roles"));
        });
    }
}
