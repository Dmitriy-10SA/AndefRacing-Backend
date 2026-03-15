package ru.andef.andefracing.backend.domain.mappers.club;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.network.dtos.common.PhotoDto;
import ru.andef.andefracing.backend.network.dtos.management.AddPhotoDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для PhotoMapper
 *
 * @see PhotoMapper
 */
class PhotoMapperTest {
    private PhotoMapper photoMapper;

    @BeforeEach
    void setUp() {
        photoMapper = Mappers.getMapper(PhotoMapper.class);
    }

    @Test
    @DisplayName("Преобразование Photo в PhotoDto")
    void testToDto() {
        // Arrange
        Photo photo = new Photo(
                1L,
                "https://example.com/photo1.jpg",
                (short) 1
        );

        // Act
        PhotoDto dto = photoMapper.toDto(photo);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("https://example.com/photo1.jpg", dto.url());
        assertEquals((short) 1, dto.sequenceNumber());
    }

    @Test
    @DisplayName("Преобразование списка Photo в список PhotoDto")
    void testToDtoList() {
        // Arrange
        List<Photo> photos = List.of(
                new Photo(1L, "https://example.com/photo1.jpg", (short) 1),
                new Photo(2L, "https://example.com/photo2.jpg", (short) 2),
                new Photo(3L, "https://example.com/photo3.jpg", (short) 3)
        );

        // Act
        List<PhotoDto> dtos = photoMapper.toDto(photos);

        // Assert
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        assertEquals(1L, dtos.get(0).id());
        assertEquals(2L, dtos.get(1).id());
        assertEquals(3L, dtos.get(2).id());
        assertEquals("https://example.com/photo1.jpg", dtos.get(0).url());
        assertEquals("https://example.com/photo2.jpg", dtos.get(1).url());
        assertEquals("https://example.com/photo3.jpg", dtos.get(2).url());
    }

    @Test
    @DisplayName("Преобразование пустого списка Photo")
    void testToDtoEmptyList() {
        // Arrange
        List<Photo> photos = new ArrayList<>();

        // Act
        List<PhotoDto> dtos = photoMapper.toDto(photos);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("Преобразование AddPhotoDto в Photo entity")
    void testToEntity() {
        // Arrange
        AddPhotoDto addPhotoDto = new AddPhotoDto(
                "https://example.com/new-photo.jpg",
                (short) 5
        );

        // Act
        Photo photo = photoMapper.toEntity(addPhotoDto);

        // Assert
        assertNotNull(photo);
        assertEquals("https://example.com/new-photo.jpg", photo.getUrl());
        assertEquals((short) 5, photo.getSequenceNumber());
    }

    @Test
    @DisplayName("Преобразование Photo с null значениями")
    void testToDtoWithNullValues() {
        // Arrange
        Photo photo = new Photo(
                1L,
                null,
                (short) 1
        );

        // Act
        PhotoDto dto = photoMapper.toDto(photo);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertNull(dto.url());
        assertEquals((short) 1, dto.sequenceNumber());
    }

    @Test
    @DisplayName("Преобразование AddPhotoDto с null url в Photo entity")
    void testToEntityWithNullUrl() {
        // Arrange
        AddPhotoDto addPhotoDto = new AddPhotoDto(
                null,
                (short) 1
        );

        // Act
        Photo photo = photoMapper.toEntity(addPhotoDto);

        // Assert
        assertNotNull(photo);
        assertNull(photo.getUrl());
        assertEquals((short) 1, photo.getSequenceNumber());
    }
}
