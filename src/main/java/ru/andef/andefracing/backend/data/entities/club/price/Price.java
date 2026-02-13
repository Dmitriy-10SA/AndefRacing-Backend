package ru.andef.andefracing.backend.data.entities.club.price;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Цена в клубе (за кол-во минут)
 */
@Entity
@Table(name = "price", schema = "info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Price price = (Price) o;
        return Objects.equals(getId(), price.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}