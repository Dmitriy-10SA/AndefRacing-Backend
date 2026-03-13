package ru.andef.andefracing.backend.domain.mappers.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для RegionMapper
 *
 * @see RegionMapper
 */
class RegionMapperTest {
    private RegionMapper regionMapper;

    @BeforeEach
    void setUp() {
        regionMapper = Mappers.getMapper(RegionMapper.class);
    }

    @Test
    @DisplayName("Преобразование Region в RegionShortDto")
    void testToShortDto() {
        // Arrange
        Region region = new Region(
                (short) 1,
                "Московская область",
                new ArrayList<>()
        );

        // Act
        RegionShortDto dto = regionMapper.toShortDto(region);

        // Assert
        assertNotNull(dto);
        assertEquals(region.getId(), dto.id());
        assertEquals(region.getName(), dto.name());
    }

    @Test
    @DisplayName("Преобразование списка Region в список RegionShortDto")
    void testToShortDtoList() {
        // Arrange
        List<Region> regions = List.of(
                new Region((short) 1, "Московская область", new ArrayList<>()),
                new Region((short) 2, "Ленинградская область", new ArrayList<>()),
                new Region((short) 3, "Свердловская область", new ArrayList<>())
        );

        // Act
        List<RegionShortDto> dtos = regionMapper.toShortDto(regions);

        // Assert
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        assertEquals(regions.get(0).getName(), dtos.get(0).name());
        assertEquals(regions.get(1).getName(), dtos.get(1).name());
        assertEquals(regions.get(2).getName(), dtos.get(2).name());
    }

    @Test
    @DisplayName("Преобразование пустого списка Region")
    void testToShortDtoEmptyList() {
        // Arrange
        List<Region> regions = new ArrayList<>();

        // Act
        List<RegionShortDto> dtos = regionMapper.toShortDto(regions);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }
}
