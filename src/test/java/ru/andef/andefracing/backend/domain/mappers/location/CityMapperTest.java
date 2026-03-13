package ru.andef.andefracing.backend.domain.mappers.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.network.dtos.common.location.CityDto;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для CityMapper
 *
 * @see CityMapper
 */
class CityMapperTest {
    private CityMapper cityMapper;
    private RegionMapper regionMapper;

    @BeforeEach
    void setUp() {
        cityMapper = Mappers.getMapper(CityMapper.class);
        regionMapper = Mappers.getMapper(RegionMapper.class);
    }

    @Test
    @DisplayName("Преобразование City в CityDto с регионом")
    void testToDto() {
        // Arrange
        Region region = new Region(
                (short) 1,
                "Московская область",
                new ArrayList<>()
        );
        City city = new City(
                (short) 10,
                region,
                "Москва"
        );

        // Act
        CityDto dto = cityMapper.toDto(city, regionMapper);

        // Assert
        assertNotNull(dto);
        assertEquals((short) 10, dto.getId());
        assertEquals("Москва", dto.getName());
        assertNotNull(dto.getRegion());
        assertEquals((short) 1, dto.getRegion().id());
        assertEquals("Московская область", dto.getRegion().name());
    }

    @Test
    @DisplayName("Преобразование City в CityShortDto")
    void testToShortDto() {
        // Arrange
        Region region = new Region(
                (short) 2,
                "Ленинградская область",
                new ArrayList<>()
        );
        City city = new City(
                (short) 20,
                region,
                "Санкт-Петербург"
        );

        // Act
        CityShortDto dto = cityMapper.toShortDto(city);

        // Assert
        assertNotNull(dto);
        assertEquals((short) 20, dto.getId());
        assertEquals("Санкт-Петербург", dto.getName());
    }

    @Test
    @DisplayName("Преобразование списка City в список CityShortDto")
    void testToShortDtoList() {
        // Arrange
        Region region = new Region((short) 1, "Московская область", new ArrayList<>());
        List<City> cities = List.of(
                new City((short) 1, region, "Москва"),
                new City((short) 2, region, "Подольск"),
                new City((short) 3, region, "Химки")
        );

        // Act
        List<CityShortDto> dtos = cityMapper.toShortDto(cities);

        // Assert
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        assertEquals("Москва", dtos.get(0).getName());
        assertEquals("Подольск", dtos.get(1).getName());
        assertEquals("Химки", dtos.get(2).getName());
    }

    @Test
    @DisplayName("Преобразование пустого списка City")
    void testToShortDtoEmptyList() {
        // Arrange
        List<City> cities = new ArrayList<>();

        // Act
        List<CityShortDto> dtos = cityMapper.toShortDto(cities);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }
}
