package ru.andef.andefracing.backend.data.entities.club.work.schedule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/**
 * День из графика работы клуба
 */
@Entity
@Table(name = "work_schedule", schema = "info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
}