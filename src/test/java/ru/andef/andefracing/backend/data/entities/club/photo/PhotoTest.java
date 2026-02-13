package ru.andef.andefracing.backend.data.entities.club.photo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для сущности Photo
 *
 * @see Photo
 */
class PhotoTest {
    @Test
    @DisplayName("Корректное создание новой фотографии")
    void testCorrectCreateNewPhoto() {
        String url = "url";
        short sequenceNumber = 1;
        Photo photo = new Photo(url, sequenceNumber);
        assertEquals(0, photo.getId());
        assertEquals(url, photo.getUrl());
        assertEquals(sequenceNumber, photo.getSequenceNumber());
    }
}