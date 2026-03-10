package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.AddWorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.UpdateWorkScheduleDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.WorkScheduleExceptionDto;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_WORK_SCHEDULE)
public class ClubWorkScheduleManagementController {
    /**
     * Добавление «дня-исключения» в график работы в выбранном текущим клубе
     */
    @PostMapping("/exceptions")
    public ResponseEntity<Void> addWorkScheduleException(
            @PathVariable int clubId,
            @RequestBody @Valid AddWorkScheduleExceptionDto addWorkScheduleExceptionDto
    ) {
        // TODO: Обработка добавления дня-исключения
        return ResponseEntity.ok().build();
    }

    /**
     * Получение исключений в расписании на неделю
     */
    @GetMapping("/exceptions")
    public ResponseEntity<List<WorkScheduleExceptionDto>> getAllWorkSchedulesExceptionsForWeek(
            @PathVariable int clubId
    ) {
        // TODO
        return null;
    }

    /**
     * Удаление «дня-исключения» в графике работы в выбранном текущим клубе
     */
    @DeleteMapping("/exceptions/{workScheduleExceptionId}")
    public ResponseEntity<Void> deleteWorkScheduleException(
            @PathVariable int clubId,
            @PathVariable long workScheduleExceptionId
    ) {
        // TODO: Обработка удаления дня-исключения
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение графика работы, а точнее времени открытия и/или закрытия в конкретный день недели
     */
    @PutMapping
    public ResponseEntity<Void> updateWorkSchedule(
            @PathVariable int clubId,
            @RequestBody @Valid UpdateWorkScheduleDto updateWorkScheduleDto
    ) {
        // TODO: Обновление расписания на день недели
        return ResponseEntity.ok().build();
    }
}