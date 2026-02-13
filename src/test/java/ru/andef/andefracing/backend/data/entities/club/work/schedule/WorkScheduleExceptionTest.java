package ru.andef.andefracing.backend.data.entities.club.work.schedule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сущности WorkScheduleException
 *
 * @see WorkScheduleException
 */
class WorkScheduleExceptionTest {
    private static final LocalDate TEST_DATE = LocalDate.of(2026, 10, 10);
    private static final LocalTime TEST_OPEN_TIME = LocalTime.of(10, 0);
    private static final LocalTime TEST_CLOSE_TIME = LocalTime.of(20, 0);

    @Test
    @DisplayName("Создание нерабочего дня-иключения без описания")
    void testAddNotWorkDayWithNullDescription() {
        WorkScheduleException workScheduleException = new WorkScheduleException(TEST_DATE, null);
        assertEquals(0, workScheduleException.getId());
        assertEquals(TEST_DATE, workScheduleException.getDate());
        assertNull(workScheduleException.getOpenTime());
        assertNull(workScheduleException.getCloseTime());
        assertFalse(workScheduleException.isWorkDay());
        assertNull(workScheduleException.getDescription());
    }

    @Test
    @DisplayName("Создание нерабочего дня-иключения с описанием")
    void testAddNotWorkDayWithDescription() {
        String description = "description";
        WorkScheduleException workScheduleException = new WorkScheduleException(TEST_DATE, description);
        assertEquals(0, workScheduleException.getId());
        assertEquals(TEST_DATE, workScheduleException.getDate());
        assertNull(workScheduleException.getOpenTime());
        assertNull(workScheduleException.getCloseTime());
        assertFalse(workScheduleException.isWorkDay());
        assertEquals(description, workScheduleException.getDescription());
    }

    @Test
    @DisplayName("Создание рабочего дня-иключения без описания")
    void testAddWorkDayWithNullDescription() {
        WorkScheduleException workScheduleException = new WorkScheduleException(
                TEST_DATE,
                TEST_OPEN_TIME,
                TEST_CLOSE_TIME,
                null
        );
        assertEquals(0, workScheduleException.getId());
        assertEquals(TEST_DATE, workScheduleException.getDate());
        assertEquals(TEST_OPEN_TIME, workScheduleException.getOpenTime());
        assertEquals(TEST_CLOSE_TIME, workScheduleException.getCloseTime());
        assertTrue(workScheduleException.isWorkDay());
        assertNull(workScheduleException.getDescription());
    }

    @Test
    @DisplayName("Создание рабочего дня-иключения с описанием")
    void testAddWorkDayWithDescription() {
        String description = "description";
        WorkScheduleException workScheduleException = new WorkScheduleException(
                TEST_DATE,
                TEST_OPEN_TIME,
                TEST_CLOSE_TIME,
                description
        );
        assertEquals(0, workScheduleException.getId());
        assertEquals(TEST_DATE, workScheduleException.getDate());
        assertEquals(TEST_OPEN_TIME, workScheduleException.getOpenTime());
        assertEquals(TEST_CLOSE_TIME, workScheduleException.getCloseTime());
        assertTrue(workScheduleException.isWorkDay());
        assertEquals(description, workScheduleException.getDescription());
    }
}