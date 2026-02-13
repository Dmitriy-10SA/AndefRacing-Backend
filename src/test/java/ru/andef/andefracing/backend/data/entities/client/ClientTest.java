package ru.andef.andefracing.backend.data.entities.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.andef.andefracing.backend.data.entities.booking.Booking;
import ru.andef.andefracing.backend.data.entities.booking.BookingStatus;
import ru.andef.andefracing.backend.data.entities.info.Club;
import ru.andef.andefracing.backend.data.entities.location.City;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сущности Client
 *
 * @see Client
 */
class ClientTest {
    private static final String TEST_CLIENT_NAME = "Dmitriy";
    private static final String TEST_CLIENT_PHONE = "+7-999-999-99-99";
    private static final String TEST_CLIENT_PASSWORD = "super-secret";

    private static final OffsetDateTime TEST_START_DATE_TIME = OffsetDateTime
            .of(LocalDateTime.of(2026, 1, 1, 10, 0), ZoneOffset.UTC);
    private static final OffsetDateTime TEST_END_DATE_TIME = TEST_START_DATE_TIME.plusDays(1);

    private static final Club CLUB = new Club();

    private static final short CNT_EQUIPMENT = 1;

    private static final BigDecimal PRICE_VALUE = new BigDecimal("1500.00");

    /**
     * Получение нового клиента
     */
    private Client getNewClient() {
        return new Client(TEST_CLIENT_NAME, TEST_CLIENT_PHONE, TEST_CLIENT_PASSWORD);
    }

    /**
     * Получение нового бронирования от клиента
     */
    private Booking getClientBooking(Client client) {
        return client.makeBooking(
                CLUB,
                TEST_START_DATE_TIME,
                TEST_END_DATE_TIME,
                CNT_EQUIPMENT,
                PRICE_VALUE
        );
    }

    /**
     * Получение клуба с id
     */
    private Club getClub(int id) {
        return new Club(
                id,
                new City(),
                "name",
                "phone",
                "email",
                "address",
                CNT_EQUIPMENT,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Test
    @DisplayName("Регистрация клиента")
    void testRegister() {
        Client client = getNewClient();
        assertEquals(0, client.getId());
        assertEquals(TEST_CLIENT_NAME, client.getName());
        assertEquals(TEST_CLIENT_PHONE, client.getPhone());
        assertEquals(TEST_CLIENT_PASSWORD, client.getPassword());
        assertFalse(client.isBlocked());
        assertEquals(0, client.getFavoriteClubs().size());
        assertEquals(0, client.getBookings().size());
    }

    @Test
    @DisplayName("Попытка сделать бронирование клиентом")
    void testMakeBooking() {
        Client client = getNewClient();
        Booking booking = getClientBooking(client);
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
    @DisplayName("Оплата бронирования клиентом")
    void testPaidBooking() {
        Client client = getNewClient();
        Booking booking = getClientBooking(client);
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        client.paidBooking(booking);
        assertEquals(BookingStatus.PAID, booking.getStatus());
    }

    @Test
    @DisplayName("Добавление клуба в список избранных клубов")
    void testAddFavoriteClub() {
        Client client = getNewClient();
        assertEquals(0, client.getFavoriteClubs().size());
        Club favoriteClub = new Club();
        client.addFavoriteClub(favoriteClub);
        assertEquals(1, client.getFavoriteClubs().size());
        assertEquals(favoriteClub, client.getFavoriteClubs().get(0));
    }

    @Test
    @DisplayName("Удаление клуба из списка избранных клубов")
    void testDeleteFavoriteClub() {
        Client client = getNewClient();
        Club favoriteClub1 = getClub(1);
        Club favoriteClub2 = getClub(2);
        Club favoriteClub3 = getClub(3);
        client.addFavoriteClub(favoriteClub1);
        client.addFavoriteClub(favoriteClub2);
        client.addFavoriteClub(favoriteClub3);
        assertEquals(3, client.getFavoriteClubs().size());
        boolean isDeleted = client.deleteFavoriteClub(favoriteClub2);
        assertTrue(isDeleted);
        assertEquals(2, client.getFavoriteClubs().size());
        assertFalse(client.getFavoriteClubs().contains(favoriteClub2));
    }

    @Test
    @DisplayName("Удаление клуба, которого нет в списке избранных клубов, из списка избранных клубов")
    void testDeleteFavoriteClubWhichDoesNotExistInFavoriteClubs() {
        Client client = getNewClient();
        Club favoriteClub1 = getClub(1);
        Club favoriteClub2 = getClub(2);
        Club favoriteClub3 = getClub(3);
        client.addFavoriteClub(favoriteClub1);
        client.addFavoriteClub(favoriteClub2);
        assertEquals(2, client.getFavoriteClubs().size());
        boolean isDeleted = client.deleteFavoriteClub(favoriteClub3);
        assertFalse(isDeleted);
        assertEquals(2, client.getFavoriteClubs().size());
        assertFalse(client.getFavoriteClubs().contains(favoriteClub3));
    }
}