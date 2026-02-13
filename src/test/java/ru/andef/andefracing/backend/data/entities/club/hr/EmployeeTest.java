package ru.andef.andefracing.backend.data.entities.club.hr;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сущности Employee
 *
 * @see Employee
 */
class EmployeeTest {
    private static final OffsetDateTime TEST_START_DATE_TIME = OffsetDateTime
            .of(LocalDateTime.of(2026, 1, 1, 10, 0), ZoneOffset.UTC);
    private static final OffsetDateTime TEST_END_DATE_TIME = TEST_START_DATE_TIME.plusDays(1);

    private static final Club CLUB = new Club();

    private static final short CNT_EQUIPMENT = 1;

    private static final BigDecimal PRICE_VALUE = new BigDecimal("1500.00");

    private static final String SURNAME = "Ivan";
    private static final String NAME = "Ivanov";
    private static final String PATRONYMIC = "Ivanovich";
    private static final String PHONE = "+7-999-999-99-99";

    /**
     * Регистрация сотрудника
     */
    private Employee getNewEmployee() {
        return new Employee(SURNAME, NAME, PATRONYMIC, PHONE);
    }

    /**
     * Получение бронирования, созданного сотрудником
     */
    private Booking getEmployeeBooking(Employee employee) {
        return employee.addBooking(
                CLUB,
                TEST_START_DATE_TIME,
                TEST_END_DATE_TIME,
                CNT_EQUIPMENT,
                PRICE_VALUE
        );
    }

    @Test
    @DisplayName("При регистрация сотрудника все данные, кроме пароля сохраняются успешно и need_password = TRUE")
    void testRegisterEmployeeWithoutPassword() {
        Employee employee = getNewEmployee();
        assertEquals(0, employee.getId());
        assertEquals(SURNAME, employee.getSurname());
        assertEquals(NAME, employee.getName());
        assertEquals(PATRONYMIC, employee.getPatronymic());
        assertEquals(PHONE, employee.getPhone());
        assertNull(employee.getPassword());
        assertTrue(employee.isNeedPassword());
        assertFalse(employee.isBlocked());
        assertTrue(employee.getClubAndRoles().isEmpty());
        assertTrue(employee.getBookings().isEmpty());
    }

    @Test
    @DisplayName("Добавление бронирования сотрудником (оплата сразу)")
    void testAddBooking() {
        Employee employee = getNewEmployee();
        Booking booking = getEmployeeBooking(employee);
        assertEquals(0, booking.getId());
        assertEquals(CLUB, booking.getClub());
        assertEquals(employee, booking.getCreatedByEmployee());
        assertEquals(TEST_START_DATE_TIME, booking.getStartDateTime());
        assertEquals(TEST_END_DATE_TIME, booking.getEndDateTime());
        assertEquals(CNT_EQUIPMENT, booking.getCntEquipment());
        assertEquals(PRICE_VALUE, booking.getPriceValue());
        assertEquals(BookingStatus.PAID, booking.getStatus());
        assertTrue(booking.isWalkIn());
        assertNull(booking.getClient());
    }

    @Test
    @DisplayName("Отмена бронирования сотрудником")
    void testCancelBooking() {
        Employee employee = getNewEmployee();
        Booking booking = getEmployeeBooking(employee);
        assertEquals(BookingStatus.PAID, booking.getStatus());
        employee.cancelBooking(booking);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    @DisplayName("Установка пароля сотрудника в первый раз")
    void testSetPassword() {
        Employee employee = getNewEmployee();
        assertNull(employee.getPassword());
        assertTrue(employee.isNeedPassword());
        String newPassword = "new-super-puper-password";
        employee.setPassword(newPassword);
        assertEquals(newPassword, employee.getPassword());
        assertFalse(employee.isNeedPassword());
    }

    @Test
    @DisplayName("Изменение пароля сотрудника")
    void testSetPasswordNotFirstAttempt() {
        Employee employee = getNewEmployee();
        String newPassword = "new-super-puper-password";
        employee.setPassword(newPassword);
        assertEquals(newPassword, employee.getPassword());
        assertFalse(employee.isNeedPassword());
        String newNewPassword = "wow-its-new-password!";
        employee.setPassword(newNewPassword);
        assertEquals(newNewPassword, employee.getPassword());
        assertFalse(employee.isNeedPassword());
    }
}