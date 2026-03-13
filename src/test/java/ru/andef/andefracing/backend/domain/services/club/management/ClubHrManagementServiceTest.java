package ru.andef.andefracing.backend.domain.services.club.management;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.club.EmployeeRepository;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.DuplicateException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.management.EmployeeWithThisPhoneAlreadyExistsException;
import ru.andef.andefracing.backend.network.dtos.management.hr.AddExistingEmployeeDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.AddNewEmployeeDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.EmployeeAndRolesDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
@Transactional
class ClubHrManagementServiceTest {
    private final ClubHrManagementService clubHrManagementService;
    private final ClubRepository clubRepository;
    private final EmployeeRepository employeeRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    @Autowired
    public ClubHrManagementServiceTest(
            ClubHrManagementService clubHrManagementService,
            ClubRepository clubRepository,
            EmployeeRepository employeeRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository
    ) {
        this.clubHrManagementService = clubHrManagementService;
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

    private Employee createEmployee(String phone) {
        Employee employee = new Employee("Surname", "Name", "Patronymic", phone);
        return employeeRepository.save(employee);
    }

    @Test
    void isEmployeeInSystemReturnsTrueWhenEmployeeExists() {
        // Arrange
        String phone = "+7-111-111-11-11";
        createEmployee(phone);

        // Act
        boolean result = clubHrManagementService.isEmployeeInSystem(phone);

        // Assert
        assertTrue(result);
    }

    @Test
    void isEmployeeInSystemReturnsFalseWhenEmployeeDoesNotExist() {
        // Arrange
        String phone = "+7-999-999-99-99";

        // Act
        boolean result = clubHrManagementService.isEmployeeInSystem(phone);

        // Assert
        assertFalse(result);
    }

    @Test
    void addNewEmployeeToClubAddsEmployeeSuccessfully() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        AddNewEmployeeDto dto = new AddNewEmployeeDto(
                "+7-111-111-11-11",
                List.of(EmployeeRole.EMPLOYEE, EmployeeRole.ADMIN),
                "Ivanov",
                "Ivan",
                "Ivanovich"
        );

        // Act
        clubHrManagementService.addNewEmployeeToClub(club.getId(), dto);

        // Assert
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(2, updatedClub.getEmployeesAndRoles().size());
        Employee savedEmployee = employeeRepository.findByPhone("+7-111-111-11-11").orElseThrow();
        assertEquals("Ivanov", savedEmployee.getSurname());
        assertEquals("Ivan", savedEmployee.getName());
        assertEquals("Ivanovich", savedEmployee.getPatronymic());
    }

    @Test
    void addNewEmployeeToClubThrowsExceptionWhenPhoneAlreadyExists() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        String phone = "+7-111-111-11-11";
        createEmployee(phone);
        AddNewEmployeeDto dto = new AddNewEmployeeDto(
                phone,
                List.of(EmployeeRole.EMPLOYEE),
                "Petrov",
                "Petr",
                "Petrovich"
        );

        // Act & Assert
        assertThrows(EmployeeWithThisPhoneAlreadyExistsException.class, () ->
                clubHrManagementService.addNewEmployeeToClub(club.getId(), dto)
        );
    }

    @Test
    void addExistingEmployeeToClubAddsEmployeeSuccessfully() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        String phone = "+7-111-111-11-11";
        Employee employee = createEmployee(phone);
        AddExistingEmployeeDto dto = new AddExistingEmployeeDto(
                phone,
                List.of(EmployeeRole.MANAGER)
        );

        // Act
        clubHrManagementService.addExistingEmployeeToClub(club.getId(), dto);

        // Assert
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(1, updatedClub.getEmployeesAndRoles().size());
        assertEquals(employee.getId(), updatedClub.getEmployeesAndRoles().get(0).getEmployee().getId());
    }

    @Test
    void addExistingEmployeeToClubThrowsExceptionWhenEmployeeAlreadyInClub() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        String phone = "+7-111-111-11-11";
        Employee employee = createEmployee(phone);
        club.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE));
        clubRepository.save(club);
        AddExistingEmployeeDto dto = new AddExistingEmployeeDto(
                phone,
                List.of(EmployeeRole.MANAGER)
        );

        // Act & Assert
        assertThrows(DuplicateException.class, () ->
                clubHrManagementService.addExistingEmployeeToClub(club.getId(), dto)
        );
    }

    @Test
    void getEmployeesAndRolesInClubReturnsCorrectList() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee1 = createEmployee("+7-111-111-11-11");
        Employee employee2 = createEmployee("+7-222-222-22-22");
        club.addEmployee(employee1, List.of(EmployeeRole.EMPLOYEE, EmployeeRole.ADMIN));
        club.addEmployee(employee2, List.of(EmployeeRole.MANAGER));
        clubRepository.save(club);

        // Act
        List<EmployeeAndRolesDto> result = clubHrManagementService.getEmployeesAndRolesInClub(club.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getEmployeesAndRolesInClubReturnsEmptyListWhenNoEmployees() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");

        // Act
        List<EmployeeAndRolesDto> result = clubHrManagementService.getEmployeesAndRolesInClub(club.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteEmployeeFromClubRemovesEmployeeSuccessfully() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");
        club.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE));
        clubRepository.save(club);

        // Act
        clubHrManagementService.deleteEmployeeFromClub(club.getId(), employee.getId());

        // Assert
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        assertTrue(updatedClub.getEmployeesAndRoles().isEmpty());
    }

    @Test
    void deleteEmployeeFromClubThrowsExceptionWhenEmployeeNotInClub() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.deleteEmployeeFromClub(club.getId(), employee.getId())
        );
    }

    @Test
    void addRoleToEmployeeInClubAddsRoleSuccessfully() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");
        club.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE));
        clubRepository.save(club);

        // Act
        clubHrManagementService.addRoleToEmployeeInClub(club.getId(), employee.getId(), EmployeeRole.ADMIN);

        // Assert
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(2, updatedClub.getEmployeesAndRoles().size());
    }

    @Test
    void addRoleToEmployeeInClubThrowsExceptionWhenEmployeeNotInClub() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.addRoleToEmployeeInClub(club.getId(), employee.getId(), EmployeeRole.ADMIN)
        );
    }

    @Test
    void addRoleToEmployeeInClubThrowsExceptionWhenRoleAlreadyExists() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");
        club.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE));
        clubRepository.save(club);

        // Act & Assert
        assertThrows(DuplicateException.class, () ->
                clubHrManagementService.addRoleToEmployeeInClub(club.getId(), employee.getId(), EmployeeRole.EMPLOYEE)
        );
    }

    @Test
    void updateEmployeeRoleInClubUpdatesRoleSuccessfully() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");
        club.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE));
        clubRepository.save(club);

        // Act
        clubHrManagementService.updateEmployeeRoleInClub(
                club.getId(),
                employee.getId(),
                EmployeeRole.EMPLOYEE,
                EmployeeRole.MANAGER
        );

        // Assert
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(1, updatedClub.getEmployeesAndRoles().size());
        assertEquals(EmployeeRole.MANAGER, updatedClub.getEmployeesAndRoles().get(0).getEmployeeRole());
    }

    @Test
    void updateEmployeeRoleInClubThrowsExceptionWhenOldRoleNotFound() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");
        club.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE));
        clubRepository.save(club);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.updateEmployeeRoleInClub(
                        club.getId(),
                        employee.getId(),
                        EmployeeRole.ADMIN,
                        EmployeeRole.MANAGER
                )
        );
    }

    @Test
    void deleteEmployeeRoleInClubDeletesRoleSuccessfully() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");
        club.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE, EmployeeRole.ADMIN));
        clubRepository.save(club);

        // Act
        clubHrManagementService.deleteEmployeeRoleInClub(club.getId(), employee.getId(), EmployeeRole.ADMIN);

        // Assert
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(1, updatedClub.getEmployeesAndRoles().size());
        assertEquals(EmployeeRole.EMPLOYEE, updatedClub.getEmployeesAndRoles().get(0).getEmployeeRole());
    }

    @Test
    void deleteEmployeeRoleInClubThrowsExceptionWhenRoleNotFound() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");
        club.addEmployee(employee, List.of(EmployeeRole.EMPLOYEE));
        clubRepository.save(club);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.deleteEmployeeRoleInClub(club.getId(), employee.getId(), EmployeeRole.ADMIN)
        );
    }

    @Test
    void deleteEmployeeRoleInClubThrowsExceptionWhenEmployeeNotInClub() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.deleteEmployeeRoleInClub(club.getId(), employee.getId(), EmployeeRole.EMPLOYEE)
        );
    }

    @Test
    void addNewEmployeeToClubThrowsExceptionWhenClubNotFound() {
        // Arrange
        int nonExistentClubId = 999;
        AddNewEmployeeDto dto = new AddNewEmployeeDto(
                "+7-111-111-11-11",
                List.of(EmployeeRole.EMPLOYEE),
                "Ivanov",
                "Ivan",
                "Ivanovich"
        );

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.addNewEmployeeToClub(nonExistentClubId, dto)
        );
    }

    @Test
    void addExistingEmployeeToClubThrowsExceptionWhenEmployeeNotFound() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        AddExistingEmployeeDto dto = new AddExistingEmployeeDto(
                "+7-999-999-99-99",
                List.of(EmployeeRole.MANAGER)
        );

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.addExistingEmployeeToClub(club.getId(), dto)
        );
    }

    @Test
    void getEmployeesAndRolesInClubThrowsExceptionWhenClubNotFound() {
        // Arrange
        int nonExistentClubId = 999;

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.getEmployeesAndRolesInClub(nonExistentClubId)
        );
    }

    @Test
    void deleteEmployeeFromClubThrowsExceptionWhenClubNotFound() {
        // Arrange
        int nonExistentClubId = 999;
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.deleteEmployeeFromClub(nonExistentClubId, employee.getId())
        );
    }

    @Test
    void addRoleToEmployeeInClubThrowsExceptionWhenClubNotFound() {
        // Arrange
        int nonExistentClubId = 999;
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.addRoleToEmployeeInClub(nonExistentClubId, employee.getId(), EmployeeRole.ADMIN)
        );
    }

    @Test
    void updateEmployeeRoleInClubThrowsExceptionWhenClubNotFound() {
        // Arrange
        int nonExistentClubId = 999;
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.updateEmployeeRoleInClub(
                        nonExistentClubId,
                        employee.getId(),
                        EmployeeRole.EMPLOYEE,
                        EmployeeRole.MANAGER
                )
        );
    }

    @Test
    void updateEmployeeRoleInClubThrowsExceptionWhenEmployeeNotInClub() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.updateEmployeeRoleInClub(
                        club.getId(),
                        employee.getId(),
                        EmployeeRole.EMPLOYEE,
                        EmployeeRole.MANAGER
                )
        );
    }

    @Test
    void deleteEmployeeRoleInClubThrowsExceptionWhenClubNotFound() {
        // Arrange
        int nonExistentClubId = 999;
        Employee employee = createEmployee("+7-111-111-11-11");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clubHrManagementService.deleteEmployeeRoleInClub(nonExistentClubId, employee.getId(), EmployeeRole.EMPLOYEE)
        );
    }

    @Test
    void addNewEmployeeToClubWithMultipleRolesAddsAllRoles() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        AddNewEmployeeDto dto = new AddNewEmployeeDto(
                "+7-111-111-11-11",
                List.of(EmployeeRole.EMPLOYEE, EmployeeRole.ADMIN, EmployeeRole.MANAGER),
                "Ivanov",
                "Ivan",
                "Ivanovich"
        );

        // Act
        clubHrManagementService.addNewEmployeeToClub(club.getId(), dto);

        // Assert
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(3, updatedClub.getEmployeesAndRoles().size());
    }

    @Test
    void addExistingEmployeeToClubWithMultipleRolesAddsAllRoles() {
        // Arrange
        Region region = createRegion();
        City city = createCity(region);
        Club club = createClub(city, "Test Club");
        String phone = "+7-111-111-11-11";
        Employee employee = createEmployee(phone);
        AddExistingEmployeeDto dto = new AddExistingEmployeeDto(
                phone,
                List.of(EmployeeRole.EMPLOYEE, EmployeeRole.ADMIN, EmployeeRole.MANAGER)
        );

        // Act
        clubHrManagementService.addExistingEmployeeToClub(club.getId(), dto);

        // Assert
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(3, updatedClub.getEmployeesAndRoles().size());
    }
}
