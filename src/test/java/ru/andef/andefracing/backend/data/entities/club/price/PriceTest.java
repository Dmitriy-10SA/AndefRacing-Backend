package ru.andef.andefracing.backend.data.entities.club.price;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для сущности Price
 *
 * @see Price
 */
class PriceTest {
    @Test
    @DisplayName("Корректное создание новой цены")
    void testCorrectCreateNewPrice() {
        short durationMinutes = 15;
        BigDecimal priceValue = new BigDecimal("700.00");
        Price price = new Price(durationMinutes, priceValue);
        assertEquals(0, price.getId());
        assertEquals(durationMinutes, price.getDurationMinutes());
        assertEquals(priceValue, price.getValue());
    }
}