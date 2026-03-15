package ru.andef.andefracing.backend.domain.mappers.club;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.WorkScheduleExceptionDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для WorkScheduleExceptionMapper
 *
 * @see WorkScheduleExceptionMapper
 */
class WorkScheduleExceptionMapperTest {
    private WorkScheduleExceptionMapper workScheduleExceptionMapper;

    @BeforeEach
    void setUp() {
        workScheduleExceptionMapper = Mappers.getMapper(WorkScheduleExceptionMapper.class);
    }

    @Test
    @DisplayName("Преобразование WorkScheduleException (рабочий день) в WorkScheduleExceptionDto")
    void testToDtoWorkingDay() {
        // Arrange
        WorkScheduleException exception = new WorkScheduleException(
                1L,
                LocalDate.of(2024, 5, 1),
                LocalTime.of(12, 0),
                LocalTime.of(18, 0),
                true,
                "Праздничный день с сокращенным графиком"
        );

        // Act
        WorkScheduleExceptionDto dto = workScheduleExceptionMapper.toDto(exception);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals(LocalDate.of(2024, 5, 1), dto.date());
        assertEquals(LocalTime.of(12, 0), dto.openTime());
        assertEquals(LocalTime.of(18, 0), dto.closeTime());
        assertTrue(dto.isWorkDay());
        assertEquals("Праздничный день с сокращенным графиком", dto.description());
    }

    @Test
    @DisplayName("Преобразование WorkScheduleException (выходной день) в WorkScheduleExceptionDto")
    void testToDtoNonWorkingDay() {
        // Arrange
        WorkScheduleException exception = new WorkScheduleException(
                2L,
                LocalDate.of(2024, 1, 1),
                null,
                null,
                false,
                "Новый год"
        );

        // Act
        WorkScheduleExceptionDto dto = workScheduleExceptionMapper.toDto(exception);

        // Assert
        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals(LocalDate.of(2024, 1, 1), dto.date());
        assertNull(dto.openTime());
        assertNull(dto.closeTime());
        assertFalse(dto.isWorkDay());
        assertEquals("Новый год", dto.description());
    }

    @Test
    @DisplayName("Преобразование списка WorkScheduleException в список WorkScheduleExceptionDto")
    void testToDtoList() {
        // Arrange
        List<WorkScheduleException> exceptions = List.of(
                new WorkScheduleException(1L, LocalDate.of(2024, 1, 1), null, null, false, "Новый год"),
                new WorkScheduleException(2L, LocalDate.of(2024, 5, 1), LocalTime.of(12, 0), LocalTime.of(18, 0), true, "Праздник труда"),
                new WorkScheduleException(3L, LocalDate.of(2024, 12, 31), LocalTime.of(10, 0), LocalTime.of(16, 0), true, "Предновогодний день")
        );

        // Act
        List<WorkScheduleExceptionDto> dtos = workScheduleExceptionMapper.toDto(exceptions);

        // Assert
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        assertEquals(LocalDate.of(2024, 1, 1), dtos.get(0).date());
        assertEquals(LocalDate.of(2024, 5, 1), dtos.get(1).date());
        assertEquals(LocalDate.of(2024, 12, 31), dtos.get(2).date());
        assertFalse(dtos.get(0).isWorkDay());
        assertTrue(dtos.get(1).isWorkDay());
        assertTrue(dtos.get(2).isWorkDay());
    }

    @Test
    @DisplayName("Преобразование пустого списка WorkScheduleException")
    void testToDtoEmptyList() {
        // Arrange
        List<WorkScheduleException> exceptions = new ArrayList<>();

        // Act
        List<WorkScheduleExceptionDto> dtos = workScheduleExceptionMapper.toDto(exceptions);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Преобразование WorkScheduleException с null описанием")
    void testToDtoWithNullDescription() {
        // Arrange
        WorkScheduleException exception = new WorkScheduleException(
                1L,
                LocalDate.of(2024, 6, 12),
                null,
                null,
                false,
                null
        );

        // Act
        WorkScheduleExceptionDto dto = workScheduleExceptionMapper.toDto(exception);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals(LocalDate.of(2024, 6, 12), dto.date());
        assertNull(dto.openTime());
        assertNull(dto.closeTime());
        assertFalse(dto.isWorkDay());
        assertNull(dto.description());
    }

    @Test
    @DisplayName("Преобразование WorkScheduleException с пустым описанием")
    void testToDtoWithEmptyDescription() {
        // Arrange
        WorkScheduleException exception = new WorkScheduleException(
                1L,
                LocalDate.of(2024, 3, 8),
                null,
                null,
                false,
                ""
        );

        // Act
        WorkScheduleExceptionDto dto = workScheduleExceptionMapper.toDto(exception);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals(LocalDate.of(2024, 3, 8), dto.date());
        assertEquals("", dto.description());
    }
}
