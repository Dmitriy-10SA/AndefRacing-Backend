package ru.andef.andefracing.backend.data.entities.info;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

/**
 * День из графика работы клуба
 */
@Entity
@Table(name = "work_schedule", schema = "info")
@Getter
@NoArgsConstructor
public class WorkSchedule {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "day_of_week", nullable = false)
    private short dayOfWeek;

    @Column(name = "open_time")
    @Setter
    private LocalTime openTime;

    @Column(name = "close_time")
    @Setter
    private LocalTime closeTime;

    @Column(name = "is_work_day", nullable = false)
    @Setter
    private boolean isWorkDay;

    /**
     * Нерабочий день в графике работы
     */
    public WorkSchedule(DayOfWeek dayOfWeek) {
        this.dayOfWeek = (short) dayOfWeek.getValue();
        this.openTime = null;
        this.closeTime = null;
        this.isWorkDay = false;
    }

    /**
     * Рабочий день в графике работы
     */
    public WorkSchedule(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        this.dayOfWeek = (short) dayOfWeek.getValue();
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isWorkDay = true;
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
        WorkSchedule that = (WorkSchedule) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}