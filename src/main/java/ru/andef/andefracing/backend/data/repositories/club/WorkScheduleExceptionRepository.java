package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkScheduleExceptionRepository extends JpaRepository<WorkScheduleException, Long> {
    /**
     * Получение дня-исключения по клубу и дате
     */
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM info.work_schedule_exception WHERE club_id = :clubId AND date = :date"
    )
    Optional<WorkScheduleException> findByClubIdAndDate(
            @Param(value = "clubId") int clubId,
            @Param(value = "date") LocalDate date
    );

    /**
     * Получение дней-исключений по клубу и диапазонам дат
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT * FROM info.work_schedule_exception
                    WHERE club_id = :clubId AND date BETWEEN :start AND :end
                    """
    )
    List<WorkScheduleException> findAllByRangeOfDatesBetweenStartAndEnd(
            @Param(value = "clubId") int clubId,
            @Param(value = "start") LocalDate start,
            @Param(value = "end") LocalDate end
    );

    /**
     * Получение дня-исключения по клубу и id дня-исключения
     */
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM info.work_schedule_exception WHERE id = :id AND club_id = :clubId"
    )
    Optional<WorkScheduleException> findByIdAndClubId(
            @Param(value = "id") long id,
            @Param(value = "clubId") int clubId
    );
}