package ru.andef.andefracing.backend.domain.mappers.club;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubShortDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.FavoriteClubShortDto;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ClubMapper
 *
 * @see ClubMapper
 */
class ClubMapperTest {
    private ClubMapper clubMapper;
    private CityMapper cityMapper;
    private RegionMapper regionMapper;
    private PhotoMapper photoMapper;
    private GameMapper gameMapper;
    private PriceMapper priceMapper;
    private WorkScheduleMapper workScheduleMapper;

    @BeforeEach
    void setUp() {
        clubMapper = Mappers.getMapper(ClubMapper.class);
        cityMapper = Mappers.getMapper(CityMapper.class);
        regionMapper = Mappers.getMapper(RegionMapper.class);
        photoMapper = Mappers.getMapper(PhotoMapper.class);
        gameMapper = Mappers.getMapper(GameMapper.class);
        priceMapper = Mappers.getMapper(PriceMapper.class);
        workScheduleMapper = Mappers.getMapper(WorkScheduleMapper.class);
    }

    private Club createTestClub() {
        Region region = new Region((short) 1, "Московская область", new ArrayList<>());
        City city = new City((short) 1, region, "Москва");

        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo(1L, "https://example.com/photo1.jpg", (short) 1));
        photos.add(new Photo(2L, "https://example.com/photo2.jpg", (short) 2));

        List<Price> prices = new ArrayList<>();
        prices.add(new Price(1L, (short) 30, new BigDecimal("500.00")));
        prices.add(new Price(2L, (short) 60, new BigDecimal("900.00")));

        List<WorkSchedule> workSchedules = new ArrayList<>();
        workSchedules.add(new WorkSchedule(1L, (short) 1, LocalTime.of(10, 0), LocalTime.of(22, 0), true));
        workSchedules.add(new WorkSchedule(2L, (short) 2, LocalTime.of(10, 0), LocalTime.of(22, 0), true));

        return new Club(
                1,
                city,
                "Test Racing Club",
                "+7-999-111-22-33",
                "test@club.com",
                "Test Address, 123",
                (short) 5,
                true,
                photos,
                new ArrayList<>(),
                new ArrayList<>(),
                prices,
                workSchedules,
                new ArrayList<>()
        );
    }

    @Test
    @DisplayName("Преобразование Club в EmployeeClubDto")
    void testToEmployeeClubDto() {
        // Arrange
        Club club = createTestClub();

        // Act
        EmployeeClubDto dto = clubMapper.toEmployeeClubDto(club, cityMapper, regionMapper);

        // Assert
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Test Racing Club", dto.getName());
        assertEquals("+7-999-111-22-33", dto.getPhone());
        assertEquals("test@club.com", dto.getEmail());
        assertEquals("Test Address, 123", dto.getAddress());
        assertEquals((short) 5, dto.getCntEquipment());
        assertTrue(dto.isOpen());
        assertNotNull(dto.getCity());
        assertEquals("Москва", dto.getCity().getName());
    }

    @Test
    @DisplayName("Преобразование списка Club в список EmployeeClubDto")
    void testToEmployeeClubDtoList() {
        // Arrange
        Club club1 = createTestClub();
        Club club2 = createTestClub();
        List<Club> clubs = List.of(club1, club2);

        // Act
        List<EmployeeClubDto> dtos = clubMapper.toEmployeeClubDto(clubs, cityMapper, regionMapper);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Test Racing Club", dtos.get(0).getName());
        assertEquals("Test Racing Club", dtos.get(1).getName());
    }

    @Test
    @DisplayName("Преобразование Club в FavoriteClubShortDto")
    void testToFavoriteClubShortDto() {
        // Arrange
        Club club = createTestClub();

        // Act
        FavoriteClubShortDto dto = clubMapper.toFavoriteClubShortDto(club, cityMapper, regionMapper, photoMapper);

        // Assert
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Test Racing Club", dto.getName());
        assertEquals("+7-999-111-22-33", dto.getPhone());
        assertEquals("test@club.com", dto.getEmail());
        assertEquals("Test Address, 123", dto.getAddress());
        assertEquals((short) 5, dto.getCntEquipment());
        assertTrue(dto.isOpen());
        assertNotNull(dto.getCity());
        assertEquals("Москва", dto.getCity().getName());
        assertNotNull(dto.getMainPhoto());
        assertEquals("https://example.com/photo1.jpg", dto.getMainPhoto().url());
    }

    @Test
    @DisplayName("Преобразование списка Club в список FavoriteClubShortDto")
    void testToFavoriteClubShortDtoList() {
        // Arrange
        Club club1 = createTestClub();
        Club club2 = createTestClub();
        List<Club> clubs = List.of(club1, club2);

        // Act
        List<FavoriteClubShortDto> dtos = clubMapper.toFavoriteClubShortDto(clubs, cityMapper, regionMapper, photoMapper);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertNotNull(dtos.get(0).getMainPhoto());
        assertNotNull(dtos.get(1).getMainPhoto());
    }

    @Test
    @DisplayName("Преобразование Club в ClubInfoDto")
    void testToInfoDto() {
        // Arrange
        Club club = createTestClub();

        // Act
        ClubInfoDto dto = clubMapper.toInfoDto(club, photoMapper);

        // Assert
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Test Racing Club", dto.getName());
        assertEquals("+7-999-111-22-33", dto.getPhone());
        assertEquals("test@club.com", dto.getEmail());
        assertEquals("Test Address, 123", dto.getAddress());
        assertEquals((short) 5, dto.getCntEquipment());
        assertTrue(dto.isOpen());
        assertNotNull(dto.getMainPhoto());
        assertEquals("https://example.com/photo1.jpg", dto.getMainPhoto().url());
    }

    @Test
    @DisplayName("Преобразование списка Club в список ClubInfoDto")
    void testToInfoDtoList() {
        // Arrange
        Club club1 = createTestClub();
        Club club2 = createTestClub();
        List<Club> clubs = List.of(club1, club2);

        // Act
        List<ClubInfoDto> dtos = clubMapper.toInfoDto(clubs, photoMapper);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Test Racing Club", dtos.get(0).getName());
        assertEquals("Test Racing Club", dtos.get(1).getName());
    }

    @Test
    @DisplayName("Преобразование Club в ClubFullInfoDto")
    void testToFullInfoDto() {
        // Arrange
        Club club = createTestClub();
        List<Game> games = List.of(
                new Game((short) 1, "Need for Speed", "https://example.com/nfs.jpg", true),
                new Game((short) 2, "Gran Turismo", "https://example.com/gt.jpg", true)
        );

        // Act
        ClubFullInfoDto dto = clubMapper.toFullInfoDto(
                club,
                games,
                photoMapper,
                gameMapper,
                priceMapper,
                workScheduleMapper
        );

        // Assert
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Test Racing Club", dto.getName());
        assertEquals("+7-999-111-22-33", dto.getPhone());
        assertEquals("test@club.com", dto.getEmail());
        assertEquals("Test Address, 123", dto.getAddress());
        assertEquals((short) 5, dto.getCntEquipment());
        assertTrue(dto.isOpen());
        assertNotNull(dto.getPhotos());
        assertEquals(2, dto.getPhotos().size());
        assertNotNull(dto.getGames());
        assertEquals(2, dto.getGames().size());
        assertNotNull(dto.getPrices());
        assertEquals(2, dto.getPrices().size());
        assertNotNull(dto.getWorkSchedules());
        assertEquals(2, dto.getWorkSchedules().size());
    }

    @Test
    @DisplayName("Преобразование Club в ClubShortDto")
    void testToShortDto() {
        // Arrange
        Club club = createTestClub();

        // Act
        ClubShortDto dto = clubMapper.toShortDto(club);

        // Assert
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Test Racing Club", dto.getName());
        assertEquals("+7-999-111-22-33", dto.getPhone());
        assertEquals("test@club.com", dto.getEmail());
        assertEquals("Test Address, 123", dto.getAddress());
        assertEquals((short) 5, dto.getCntEquipment());
        assertTrue(dto.isOpen());
    }

    @Test
    @DisplayName("Преобразование Club с isOpen = false")
    void testToShortDtoClosedClub() {
        // Arrange
        Club club = createTestClub();
        club.setOpen(false);

        // Act
        ClubShortDto dto = clubMapper.toShortDto(club);

        // Assert
        assertNotNull(dto);
        assertFalse(dto.isOpen());
    }

    @Test
    @DisplayName("Преобразование пустого списка Club")
    void testToEmployeeClubDtoEmptyList() {
        // Arrange
        List<Club> clubs = new ArrayList<>();

        // Act
        List<EmployeeClubDto> dtos = clubMapper.toEmployeeClubDto(clubs, cityMapper, regionMapper);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Преобразование Club с пустыми коллекциями")
    void testToFullInfoDtoWithEmptyCollections() {
        // Arrange
        Region region = new Region((short) 1, "Московская область", new ArrayList<>());
        City city = new City((short) 1, region, "Москва");
        Club club = new Club(
                1,
                city,
                "Empty Club",
                "+7-999-111-22-33",
                "empty@club.com",
                "Empty Address",
                (short) 3,
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        List<Game> games = new ArrayList<>();

        // Act
        ClubFullInfoDto dto = clubMapper.toFullInfoDto(
                club,
                games,
                photoMapper,
                gameMapper,
                priceMapper,
                workScheduleMapper
        );

        // Assert
        assertNotNull(dto);
        assertEquals("Empty Club", dto.getName());
        assertNotNull(dto.getPhotos());
        assertTrue(dto.getPhotos().isEmpty());
        assertNotNull(dto.getGames());
        assertTrue(dto.getGames().isEmpty());
        assertNotNull(dto.getPrices());
        assertTrue(dto.getPrices().isEmpty());
        assertNotNull(dto.getWorkSchedules());
        assertTrue(dto.getWorkSchedules().isEmpty());
    }
}
