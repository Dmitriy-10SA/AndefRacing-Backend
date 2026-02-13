package ru.andef.andefracing.backend.data.entities.bookings;

import lombok.Getter;

/**
 * Статус бронирования
 */
public enum BookingStatus {
    PENDING("Ожидание оплаты"),
    PAID("Оплачено"),
    CANCELLED("Отменено"),
    EXPIRED("Истекло время для оплаты");

    @Getter
    private final String ru;

    BookingStatus(String ru) {
        this.ru = ru;
    }
}