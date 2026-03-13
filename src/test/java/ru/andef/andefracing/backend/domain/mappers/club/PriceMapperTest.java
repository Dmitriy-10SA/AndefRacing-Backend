package ru.andef.andefracing.backend.domain.mappers.club;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;
import ru.andef.andefracing.backend.network.dtos.search.PriceDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для PriceMapper
 *
 * @see PriceMapper
 */
class PriceMapperTest {
    private PriceMapper priceMapper;

    @BeforeEach
    void setUp() {
        priceMapper = Mappers.getMapper(PriceMapper.class);
    }

    @Test
    @DisplayName("Преобразование Price в PriceDto")
    void testToDto() {
        // Arrange
        Price price = new Price(
                1L,
                (short) 30,
                new BigDecimal("500.00")
        );

        // Act
        PriceDto dto = priceMapper.toDto(price);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals((short) 30, dto.durationMinutes());
        assertEquals(new BigDecimal("500.00"), dto.value());
    }

    @Test
    @DisplayName("Преобразование списка Price в список PriceDto")
    void testToDtoList() {
        // Arrange
        List<Price> prices = List.of(
                new Price(1L, (short) 30, new BigDecimal("500.00")),
                new Price(2L, (short) 60, new BigDecimal("900.00")),
                new Price(3L, (short) 120, new BigDecimal("1600.00"))
        );

        // Act
        List<PriceDto> dtos = priceMapper.toDto(prices);

        // Assert
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        assertEquals((short) 30, dtos.get(0).durationMinutes());
        assertEquals((short) 60, dtos.get(1).durationMinutes());
        assertEquals((short) 120, dtos.get(2).durationMinutes());
        assertEquals(new BigDecimal("500.00"), dtos.get(0).value());
        assertEquals(new BigDecimal("900.00"), dtos.get(1).value());
        assertEquals(new BigDecimal("1600.00"), dtos.get(2).value());
    }

    @Test
    @DisplayName("Преобразование пустого списка Price")
    void testToDtoEmptyList() {
        // Arrange
        List<Price> prices = new ArrayList<>();

        // Act
        List<PriceDto> dtos = priceMapper.toDto(prices);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Преобразование AddPriceDto в Price entity")
    void testToEntity() {
        // Arrange
        AddPriceDto addPriceDto = new AddPriceDto(
                (short) 45,
                new BigDecimal("750.00")
        );

        // Act
        Price price = priceMapper.toEntity(addPriceDto);

        // Assert
        assertNotNull(price);
        assertEquals((short) 45, price.getDurationMinutes());
        assertEquals(new BigDecimal("750.00"), price.getValue());
    }

    @Test
    @DisplayName("Преобразование Price с null значением")
    void testToDtoWithNullValue() {
        // Arrange
        Price price = new Price(
                1L,
                (short) 30,
                null
        );

        // Act
        PriceDto dto = priceMapper.toDto(price);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals((short) 30, dto.durationMinutes());
        assertNull(dto.value());
    }

    @Test
    @DisplayName("Преобразование AddPriceDto с null значением в Price entity")
    void testToEntityWithNullValue() {
        // Arrange
        AddPriceDto addPriceDto = new AddPriceDto(
                (short) 30,
                null
        );

        // Act
        Price price = priceMapper.toEntity(addPriceDto);

        // Assert
        assertNotNull(price);
        assertEquals((short) 30, price.getDurationMinutes());
        assertNull(price.getValue());
    }
}
