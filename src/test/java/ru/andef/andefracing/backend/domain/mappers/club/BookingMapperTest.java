package ru.andef.andefracing.backend.domain.mappers.club;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.domain.mappers.ClientMapperImpl;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapperImpl;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapperImpl;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingShortDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для BookingMapper
 *
 * @see BookingMapper
 */
class BookingMapperTest {
    private BookingMapper bookingMapper;
    private ClubMapper clubMapper;
    private CityMapper cityMapper;
    private RegionMapper regionMapper;
    private ClientMapper clientMapper;


    @BeforeEach
    void setUp() {
        cityMapper = new CityMapperImpl();
        regionMapper = new RegionMapperImpl();
        clubMapper = new ClubMapperImpl();
        clientMapper = new ClientMapperImpl();
        bookingMapper = new BookingMapperImpl();
    }

    private Booking createTestBooking() {
        Club club = getClub();
        Client client = new Client(
                1L,
                "Иван",
                "+7-999-123-45-67",
                "password",
                false,
                null,
                null
        );

        return new Booking(
                1L,
                club,
                client,
                OffsetDateTime.of(LocalDateTime.of(2024, 6, 15, 14, 0), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2024, 6, 15, 15, 0), ZoneOffset.UTC),
                (short) 2,
                new BigDecimal("1500.00"),
                BookingStatus.PENDING_PAYMENT,
                false,
                null,
                null,
                "Test note"
        );
    }

    private static @NonNull Club getClub() {
        Region region = new Region((short) 1, "Московская область", new ArrayList<>());
        City city = new City((short) 1, region, "Москва");
        return new Club(
                1,
                city,
                "Test Club",
                "+7-999-111-22-33",
                "test@club.com",
                "Test Address",
                (short) 5,
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Test
    @DisplayName("Преобразование Booking в EmployeeBookingShortDto")
    void testToEmployeeBookingShortDto() {
        // Arrange
        Booking booking = createTestBooking();

        // Act
        EmployeeBookingShortDto dto = bookingMapper.toEmployeeBookingShortDto(booking);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(booking.getStartDateTime(), dto.getStartDateTime());
        assertEquals(booking.getEndDateTime(), dto.getEndDateTime());
        assertEquals(BookingStatus.PENDING_PAYMENT, dto.getStatus());
    }

    @Test
    @DisplayName("Преобразование списка Booking в список EmployeeBookingShortDto")
    void testToEmployeeBookingShortDtoList() {
        // Arrange
        Booking booking1 = createTestBooking();
        Booking booking2 = createTestBooking();
        List<Booking> bookings = List.of(booking1, booking2);

        // Act
        List<EmployeeBookingShortDto> dtos = bookingMapper.toEmployeeBookingShortDto(bookings);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(1L, dtos.get(1).getId());
    }

    @Test
    @DisplayName("Преобразование Booking в ClientBookingShortDto")
    void testToClientBookingShortDto() {
        // Arrange
        Booking booking = createTestBooking();

        // Act
        ClientBookingShortDto dto = bookingMapper.toClientBookingShortDto(
                booking,
                clubMapper,
                cityMapper,
                regionMapper
        );

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(booking.getStartDateTime(), dto.getStartDateTime());
        assertEquals(booking.getEndDateTime(), dto.getEndDateTime());
        assertEquals(BookingStatus.PENDING_PAYMENT, dto.getStatus());
        assertNotNull(dto.getClub());
        assertEquals("Test Club", dto.getClub().getName());
        assertNotNull(dto.getCity());
        assertEquals("Москва", dto.getCity().getName());
    }

    @Test
    @DisplayName("Преобразование списка Booking в список ClientBookingShortDto")
    void testToClientBookingShortDtoList() {
        // Arrange
        Booking booking1 = createTestBooking();
        Booking booking2 = createTestBooking();
        List<Booking> bookings = List.of(booking1, booking2);

        // Act
        List<ClientBookingShortDto> dtos = bookingMapper.toClientBookingShortDto(
                bookings,
                clubMapper,
                cityMapper,
                regionMapper
        );

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(1L, dtos.get(1).getId());
        assertNotNull(dtos.get(0).getClub());
        assertNotNull(dtos.get(1).getClub());
    }

    @Test
    @DisplayName("Преобразование Booking в EmployeeBookingFullInfoDto")
    void testToEmployeeBookingFullInfoDto() {
        // Arrange
        Booking booking = createTestBooking();

        // Act
        EmployeeBookingFullInfoDto dto = bookingMapper.toEmployeeBookingFullInfoDto(booking, clientMapper);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(booking.getStartDateTime(), dto.getStartDateTime());
        assertEquals(booking.getEndDateTime(), dto.getEndDateTime());
        assertEquals(BookingStatus.PENDING_PAYMENT, dto.getStatus());
        assertEquals((short) 2, dto.getCntEquipment());
        assertEquals(new BigDecimal("1500.00"), dto.getPrice());
        assertEquals("Test note", dto.getNote());
        assertNotNull(dto.getClient());
        assertEquals("Иван", dto.getClient().name());
    }

    @Test
    @DisplayName("Преобразование Booking в ClientBookingFullInfoDto")
    void testToClientBookingFullInfoDto() {
        // Arrange
        Booking booking = createTestBooking();

        // Act
        ClientBookingFullInfoDto dto = bookingMapper.toClientBookingFullInfoDto(booking, clientMapper, clubMapper, cityMapper, regionMapper);

        // Assert
        assertNotNull(dto);
        // Проверяем, что DTO создан (детальная проверка зависит от структуры DTO)
        assertNotNull(dto);
    }

    @Test
    @DisplayName("Преобразование Booking с разными статусами")
    void testToEmployeeBookingShortDtoWithDifferentStatuses() {
        // Arrange
        Booking pendingBooking = createTestBooking();

        Booking paidBooking = createTestBooking();
        paidBooking.confirmPay(null);

        Booking cancelledBooking = createTestBooking();
        cancelledBooking.cancel();

        // Act
        EmployeeBookingShortDto pendingDto = bookingMapper.toEmployeeBookingShortDto(pendingBooking);
        EmployeeBookingShortDto paidDto = bookingMapper.toEmployeeBookingShortDto(paidBooking);
        EmployeeBookingShortDto cancelledDto = bookingMapper.toEmployeeBookingShortDto(cancelledBooking);

        // Assert
        assertEquals(BookingStatus.PENDING_PAYMENT, pendingDto.getStatus());
        assertEquals(BookingStatus.PAID, paidDto.getStatus());
        assertEquals(BookingStatus.CANCELLED, cancelledDto.getStatus());
    }

    @Test
    @DisplayName("Преобразование пустого списка Booking")
    void testToEmployeeBookingShortDtoEmptyList() {
        // Arrange
        List<Booking> bookings = new ArrayList<>();

        // Act
        List<EmployeeBookingShortDto> dtos = bookingMapper.toEmployeeBookingShortDto(bookings);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Преобразование Booking с null note")
    void testToEmployeeBookingFullInfoDtoWithNullNote() {
        // Arrange
        Booking booking = createTestBooking();
        Booking bookingWithNullNote = new Booking(
                booking.getId(),
                booking.getClub(),
                booking.getClient(),
                booking.getStartDateTime(),
                booking.getEndDateTime(),
                booking.getCntEquipment(),
                booking.getPriceValue(),
                booking.getStatus(),
                booking.isWalkIn(),
                booking.getCreatedByEmployee(),
                booking.getPayConfirmedByEmployee(),
                null
        );

        // Act
        EmployeeBookingFullInfoDto dto = bookingMapper.toEmployeeBookingFullInfoDto(bookingWithNullNote, clientMapper);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getNote());
    }
}
