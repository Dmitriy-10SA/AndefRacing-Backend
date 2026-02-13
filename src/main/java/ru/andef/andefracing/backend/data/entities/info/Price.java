package ru.andef.andefracing.backend.data.entities.info;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Цена в клубе (за кол-во минут)
 */
@Entity
@Table(name = "price", schema = "info")
@Getter
@NoArgsConstructor
public class Price {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "duration_minutes", nullable = false)
    private short durationMinutes;

    @Column(name = "value", nullable = false, precision = 8, scale = 2)
    @Setter
    private BigDecimal value;

    public Price(short durationMinutes, BigDecimal value) {
        this.durationMinutes = durationMinutes;
        this.value = value;
    }
}