package ru.andef.andefracing.backend.domain.mappers.club;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для GameMapper
 *
 * @see GameMapper
 */
class GameMapperTest {
    private GameMapper gameMapper;

    @BeforeEach
    void setUp() {
        gameMapper = Mappers.getMapper(GameMapper.class);
    }

    @Test
    @DisplayName("Преобразование Game в GameDto")
    void testToDto() {
        // Arrange
        Game game = new Game(
                (short) 1,
                "Need for Speed",
                "https://example.com/nfs.jpg",
                true
        );

        // Act
        GameDto dto = gameMapper.toDto(game);

        // Assert
        assertNotNull(dto);
        assertEquals((short) 1, dto.id());
        assertEquals("Need for Speed", dto.name());
        assertEquals("https://example.com/nfs.jpg", dto.photoUrl());
        assertTrue(dto.isActive());
    }

    @Test
    @DisplayName("Преобразование Game с isActive = false")
    void testToDtoInactive() {
        // Arrange
        Game game = new Game(
                (short) 2,
                "Gran Turismo",
                "https://example.com/gt.jpg",
                false
        );

        // Act
        GameDto dto = gameMapper.toDto(game);

        // Assert
        assertNotNull(dto);
        assertEquals((short) 2, dto.id());
        assertEquals("Gran Turismo", dto.name());
        assertEquals("https://example.com/gt.jpg", dto.photoUrl());
        assertFalse(dto.isActive());
    }

    @Test
    @DisplayName("Преобразование списка Game в список GameDto")
    void testToDtoList() {
        // Arrange
        List<Game> games = List.of(
                new Game((short) 1, "Need for Speed", "https://example.com/nfs.jpg", true),
                new Game((short) 2, "Gran Turismo", "https://example.com/gt.jpg", true),
                new Game((short) 3, "Forza Horizon", "https://example.com/forza.jpg", false)
        );

        // Act
        List<GameDto> dtos = gameMapper.toDto(games);

        // Assert
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        assertEquals("Need for Speed", dtos.get(0).name());
        assertEquals("Gran Turismo", dtos.get(1).name());
        assertEquals("Forza Horizon", dtos.get(2).name());
        assertTrue(dtos.get(0).isActive());
        assertTrue(dtos.get(1).isActive());
        assertFalse(dtos.get(2).isActive());
    }

    @Test
    @DisplayName("Преобразование пустого списка Game")
    void testToDtoEmptyList() {
        // Arrange
        List<Game> games = new ArrayList<>();

        // Act
        List<GameDto> dtos = gameMapper.toDto(games);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Преобразование Game с null значениями")
    void testToDtoWithNullValues() {
        // Arrange
        Game game = new Game(
                (short) 1,
                null,
                null,
                true
        );

        // Act
        GameDto dto = gameMapper.toDto(game);

        // Assert
        assertNotNull(dto);
        assertEquals((short) 1, dto.id());
        assertNull(dto.name());
        assertNull(dto.photoUrl());
        assertTrue(dto.isActive());
    }
}
