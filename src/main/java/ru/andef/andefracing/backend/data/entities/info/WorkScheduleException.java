package ru.andef.andefracing.backend.data.entities.info;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * День-исключение в графике работы
 */
@Entity
@Table(name = "work_schedule_exception", schema = "info")
@Getter
@NoArgsConstructor
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
}