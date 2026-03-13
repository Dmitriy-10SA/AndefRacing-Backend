package ru.andef.andefracing.backend.domain.mappers.club;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.network.dtos.management.hr.EmployeeDto;
import ru.andef.andefracing.backend.network.dtos.profile.employee.EmployeePersonalInfoDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для EmployeeMapper
 *
 * @see EmployeeMapper
 */
class EmployeeMapperTest {
    private EmployeeMapper employeeMapper;

    @BeforeEach
    void setUp() {
        employeeMapper = Mappers.getMapper(EmployeeMapper.class);
    }

    @Test
    @DisplayName("Преобразование Employee в EmployeePersonalInfoDto с ролями")
    void testToPersonalInfo() {
        // Arrange
        Employee employee = new Employee(
                1L,
                "Иванов",
                "Иван",
                "Иванович",
                "+7-999-123-45-67",
                "password",
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
        List<EmployeeRole> roles = List.of(EmployeeRole.ADMIN, EmployeeRole.MANAGER);

        // Act
        EmployeePersonalInfoDto dto = employeeMapper.toPersonalInfo(employee, roles);

        // Assert
        assertNotNull(dto);
        assertEquals("+7-999-123-45-67", dto.getPhone());
        assertEquals("Иван", dto.getName());
        assertEquals("Иванов", dto.getSurname());
        assertEquals("Иванович", dto.getPatronymic());
        assertNotNull(dto.getRoles());
        assertEquals(2, dto.getRoles().size());
        assertTrue(dto.getRoles().contains(EmployeeRole.ADMIN));
        assertTrue(dto.getRoles().contains(EmployeeRole.MANAGER));
    }

    @Test
    @DisplayName("Преобразование Employee в EmployeePersonalInfoDto без отчества")
    void testToPersonalInfoWithoutPatronymic() {
        // Arrange
        Employee employee = new Employee(
                2L,
                "Петров",
                "Петр",
                null,
                "+7-999-987-65-43",
                "password",
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
        List<EmployeeRole> roles = List.of(EmployeeRole.EMPLOYEE);

        // Act
        EmployeePersonalInfoDto dto = employeeMapper.toPersonalInfo(employee, roles);

        // Assert
        assertNotNull(dto);
        assertEquals("+7-999-987-65-43", dto.getPhone());
        assertEquals("Петр", dto.getName());
        assertEquals("Петров", dto.getSurname());
        assertNull(dto.getPatronymic());
        assertNotNull(dto.getRoles());
        assertEquals(1, dto.getRoles().size());
        assertTrue(dto.getRoles().contains(EmployeeRole.EMPLOYEE));
    }

    @Test
    @DisplayName("Преобразование Employee в EmployeePersonalInfoDto с пустым списком ролей")
    void testToPersonalInfoWithEmptyRoles() {
        // Arrange
        Employee employee = new Employee(
                3L,
                "Сидоров",
                "Сидор",
                "Сидорович",
                "+7-999-111-22-33",
                "password",
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
        List<EmployeeRole> roles = new ArrayList<>();

        // Act
        EmployeePersonalInfoDto dto = employeeMapper.toPersonalInfo(employee, roles);

        // Assert
        assertNotNull(dto);
        assertEquals("+7-999-111-22-33", dto.getPhone());
        assertEquals("Сидор", dto.getName());
        assertEquals("Сидоров", dto.getSurname());
        assertEquals("Сидорович", dto.getPatronymic());
        assertNotNull(dto.getRoles());
        assertTrue(dto.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Преобразование Employee в EmployeeDto")
    void testToDto() {
        // Arrange
        Employee employee = new Employee(
                100L,
                "Алексеев",
                "Алексей",
                "Алексеевич",
                "+7-999-444-55-66",
                "password",
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );

        // Act
        EmployeeDto dto = employeeMapper.toDto(employee);

        // Assert
        assertNotNull(dto);
        assertEquals("+7-999-444-55-66", dto.phone());
        assertEquals(100L, dto.id());
        assertEquals("Алексей", dto.name());
        assertEquals("Алексеев", dto.surname());
        assertEquals("Алексеевич", dto.patronymic());
    }

    @Test
    @DisplayName("Преобразование Employee в EmployeeDto без отчества")
    void testToDtoWithoutPatronymic() {
        // Arrange
        Employee employee = new Employee(
                200L,
                "Николаев",
                "Николай",
                null,
                "+7-999-777-88-99",
                "password",
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );

        // Act
        EmployeeDto dto = employeeMapper.toDto(employee);

        // Assert
        assertNotNull(dto);
        assertEquals("+7-999-777-88-99", dto.phone());
        assertEquals(200L, dto.id());
        assertEquals("Николай", dto.name());
        assertEquals("Николаев", dto.surname());
        assertNull(dto.patronymic());
    }

    @Test
    @DisplayName("Преобразование Employee со всеми ролями")
    void testToPersonalInfoWithAllRoles() {
        // Arrange
        Employee employee = new Employee(
                1L,
                "Тестов",
                "Тест",
                "Тестович",
                "+7-999-000-00-00",
                "password",
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
        List<EmployeeRole> roles = List.of(
                EmployeeRole.EMPLOYEE,
                EmployeeRole.ADMIN,
                EmployeeRole.MANAGER
        );

        // Act
        EmployeePersonalInfoDto dto = employeeMapper.toPersonalInfo(employee, roles);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getRoles());
        assertEquals(3, dto.getRoles().size());
        assertTrue(dto.getRoles().contains(EmployeeRole.EMPLOYEE));
        assertTrue(dto.getRoles().contains(EmployeeRole.ADMIN));
        assertTrue(dto.getRoles().contains(EmployeeRole.MANAGER));
    }
}
