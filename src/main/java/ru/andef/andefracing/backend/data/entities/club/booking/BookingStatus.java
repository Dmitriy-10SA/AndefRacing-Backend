package ru.andef.andefracing.backend.data.entities.club.booking;

import lombok.Getter;

/**
 * Статус бронирования
 *
 * @see Booking бронирование
 */
public enum BookingStatus {
    PAID("Оплачено"),
    CANCELLED("Отменено");

    /**
     * Представление в виде текста на русском языке
     */
    @Getter
    private final String ru;

    BookingStatus(String ru) {
        this.ru = ru;
    }
}