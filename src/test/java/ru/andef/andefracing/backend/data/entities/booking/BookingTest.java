package ru.andef.andefracing.backend.data.entities.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.andef.andefracing.backend.data.entities.client.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сущности Booking
 *
 * @see Booking
 */
class BookingTest {
    private static final OffsetDateTime TEST_START_DATE_TIME = OffsetDateTime
            .of(LocalDateTime.of(2026, 1, 1, 10, 0), ZoneOffset.UTC);
    private static final OffsetDateTime TEST_END_DATE_TIME = TEST_START_DATE_TIME.plusDays(1);

    private static final Club CLUB = new Club();

    private static final short CNT_EQUIPMENT = 1;

    private static final BigDecimal PRICE_VALUE = new BigDecimal("1500.00");

    /**
     * Получение бронирования от клиента
     */
    private Booking getBookingByClient(Client client) {
        return new Booking(CLUB, client, TEST_START_DATE_TIME, TEST_END_DATE_TIME, CNT_EQUIPMENT, PRICE_VALUE);
    }

    @Test
    @DisplayName("Бронирование клиентом содержит всю нужную информацию")
    void testCreateBookingByClient() {
        Client client = new Client();
        Booking booking = getBookingByClient(client);
        assertEquals(0, booking.getId());
        assertEquals(CLUB, booking.getClub());
        assertEquals(client, booking.getClient());
        assertEquals(TEST_START_DATE_TIME, booking.getStartDateTime());
        assertEquals(TEST_END_DATE_TIME, booking.getEndDateTime());
        assertEquals(CNT_EQUIPMENT, booking.getCntEquipment());
        assertEquals(PRICE_VALUE, booking.getPriceValue());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertFalse(booking.isWalkIn());
        assertNull(booking.getCreatedByEmployee());
    }

    @Test
    @DisplayName("Бронирование сотрудником содержит всю нужную информацию")
    void testCreateBookingByEmployee() {
        Employee employee = new Employee();
        Booking booking = new Booking(
                CLUB,
                TEST_START_DATE_TIME,
                TEST_END_DATE_TIME,
                CNT_EQUIPMENT,
                PRICE_VALUE,
                employee
        );
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
    @DisplayName("Оплата бронирования переводит статус бронирования в PAID")
    void testPaid() {
        Booking booking = getBookingByClient(new Client());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        booking.paid();
        assertEquals(BookingStatus.PAID, booking.getStatus());
    }

    @Test
    @DisplayName("Отмена бронирования переводит статус бронирования в CANCELLED")
    void testCancel() {
        Booking booking = getBookingByClient(new Client());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        booking.cancel();
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }
}