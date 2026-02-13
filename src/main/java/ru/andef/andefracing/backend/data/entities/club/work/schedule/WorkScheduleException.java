package ru.andef.andefracing.backend.data.entities.club.work.schedule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * День-исключение в графике работы
 */
@Entity
@Table(name = "work_schedule_exception", schema = "info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleException {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "is_work_day", nullable = false)
    private boolean isWorkDay;

    @Column(name = "description")
    private String description;

    /**
     * Нерабочий день-исключение
     */
    public WorkScheduleException(LocalDate date, String description) {
        this.date = date;
        this.openTime = null;
        this.closeTime = null;
        this.isWorkDay = false;
        this.description = description;
    }

    /**
     * Рабочий день-исключение
     */
    public WorkScheduleException(LocalDate date, LocalTime openTime, LocalTime closeTime, String description) {
        this.date = date;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isWorkDay = true;
        this.description = description;
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
        WorkScheduleException that = (WorkScheduleException) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}