package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;

public interface WorkScheduleExceptionRepository extends JpaRepository<WorkScheduleException, Long> {
}