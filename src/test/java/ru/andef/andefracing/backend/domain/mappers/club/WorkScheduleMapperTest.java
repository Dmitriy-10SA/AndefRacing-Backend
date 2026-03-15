package ru.andef.andefracing.backend.domain.mappers.club;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.network.dtos.search.WorkScheduleDto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для WorkScheduleMapper
 *
 * @see WorkScheduleMapper
 */
class WorkScheduleMapperTest {
    private WorkScheduleMapper workScheduleMapper;

    @BeforeEach
    void setUp() {
        workScheduleMapper = Mappers.getMapper(WorkScheduleMapper.class);
    }

    @Test
    @DisplayName("Преобразование WorkSchedule (рабочий день) в WorkScheduleDto")
    void testToDtoWorkingDay() {
        // Arrange
        WorkSchedule workSchedule = new WorkSchedule(
                1L,
                (short) 1, // Monday
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                true
        );

        // Act
        WorkScheduleDto dto = workScheduleMapper.toDto(workSchedule);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals(DayOfWeek.MONDAY, dto.dayOfWeek());
        assertEquals(LocalTime.of(10, 0), dto.openTime());
        assertEquals(LocalTime.of(22, 0), dto.closeTime());
        assertTrue(dto.isWorkDay());
    }

    @Test
    @DisplayName("Преобразование WorkSchedule (выходной день) в WorkScheduleDto")
    void testToDtoNonWorkingDay() {
        // Arrange
        WorkSchedule workSchedule = new WorkSchedule(
                2L,
                (short) 7, // Sunday
                null,
                null,
                false
        );

        // Act
        WorkScheduleDto dto = workScheduleMapper.toDto(workSchedule);

        // Assert
        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals(DayOfWeek.SUNDAY, dto.dayOfWeek());
        assertNull(dto.openTime());
        assertNull(dto.closeTime());
        assertFalse(dto.isWorkDay());
    }

    @Test
    @DisplayName("Преобразование списка WorkSchedule в список WorkScheduleDto")
    void testToDtoList() {
        // Arrange
        List<WorkSchedule> workSchedules = List.of(
                new WorkSchedule(1L, (short) 1, LocalTime.of(10, 0), LocalTime.of(22, 0), true),
                new WorkSchedule(2L, (short) 2, LocalTime.of(10, 0), LocalTime.of(22, 0), true),
                new WorkSchedule(3L, (short) 3, LocalTime.of(10, 0), LocalTime.of(22, 0), true),
                new WorkSchedule(4L, (short) 4, LocalTime.of(10, 0), LocalTime.of(22, 0), true),
                new WorkSchedule(5L, (short) 5, LocalTime.of(10, 0), LocalTime.of(22, 0), true),
                new WorkSchedule(6L, (short) 6, null, null, false),
                new WorkSchedule(7L, (short) 7, null, null, false)
        );

        // Act
        List<WorkScheduleDto> dtos = workScheduleMapper.toDto(workSchedules);

        // Assert
        assertNotNull(dtos);
        assertEquals(7, dtos.size());
        assertEquals(DayOfWeek.MONDAY, dtos.get(0).dayOfWeek());
        assertEquals(DayOfWeek.TUESDAY, dtos.get(1).dayOfWeek());
        assertEquals(DayOfWeek.WEDNESDAY, dtos.get(2).dayOfWeek());
        assertEquals(DayOfWeek.THURSDAY, dtos.get(3).dayOfWeek());
        assertEquals(DayOfWeek.FRIDAY, dtos.get(4).dayOfWeek());
        assertEquals(DayOfWeek.SATURDAY, dtos.get(5).dayOfWeek());
        assertEquals(DayOfWeek.SUNDAY, dtos.get(6).dayOfWeek());
        assertTrue(dtos.get(0).isWorkDay());
        assertFalse(dtos.get(5).isWorkDay());
        assertFalse(dtos.get(6).isWorkDay());
    }

    @Test
    @DisplayName("Преобразование пустого списка WorkSchedule")
    void testToDtoEmptyList() {
        // Arrange
        List<WorkSchedule> workSchedules = new ArrayList<>();

        // Act
        List<WorkScheduleDto> dtos = workScheduleMapper.toDto(workSchedules);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Преобразование всех дней недели")
    void testToDtoAllDaysOfWeek() {
        // Arrange
        WorkSchedule monday = new WorkSchedule(1L, (short) 1, LocalTime.of(9, 0), LocalTime.of(21, 0), true);
        WorkSchedule tuesday = new WorkSchedule(2L, (short) 2, LocalTime.of(9, 0), LocalTime.of(21, 0), true);
        WorkSchedule wednesday = new WorkSchedule(3L, (short) 3, LocalTime.of(9, 0), LocalTime.of(21, 0), true);
        WorkSchedule thursday = new WorkSchedule(4L, (short) 4, LocalTime.of(9, 0), LocalTime.of(21, 0), true);
        WorkSchedule friday = new WorkSchedule(5L, (short) 5, LocalTime.of(9, 0), LocalTime.of(21, 0), true);
        WorkSchedule saturday = new WorkSchedule(6L, (short) 6, LocalTime.of(11, 0), LocalTime.of(20, 0), true);
        WorkSchedule sunday = new WorkSchedule(7L, (short) 7, null, null, false);

        // Act
        WorkScheduleDto mondayDto = workScheduleMapper.toDto(monday);
        WorkScheduleDto tuesdayDto = workScheduleMapper.toDto(tuesday);
        WorkScheduleDto wednesdayDto = workScheduleMapper.toDto(wednesday);
        WorkScheduleDto thursdayDto = workScheduleMapper.toDto(thursday);
        WorkScheduleDto fridayDto = workScheduleMapper.toDto(friday);
        WorkScheduleDto saturdayDto = workScheduleMapper.toDto(saturday);
        WorkScheduleDto sundayDto = workScheduleMapper.toDto(sunday);

        // Assert
        assertEquals(DayOfWeek.MONDAY, mondayDto.dayOfWeek());
        assertEquals(DayOfWeek.TUESDAY, tuesdayDto.dayOfWeek());
        assertEquals(DayOfWeek.WEDNESDAY, wednesdayDto.dayOfWeek());
        assertEquals(DayOfWeek.THURSDAY, thursdayDto.dayOfWeek());
        assertEquals(DayOfWeek.FRIDAY, fridayDto.dayOfWeek());
        assertEquals(DayOfWeek.SATURDAY, saturdayDto.dayOfWeek());
        assertEquals(DayOfWeek.SUNDAY, sundayDto.dayOfWeek());
    }
}
