package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.dtos.management.AddWorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.dtos.management.UpdateWorkScheduleDto;

@RestController
@RequestMapping("/club-management/work-schedule")
public class ClubWorkScheduleManagementController {
    /**
     * Добавление «дня-исключения» в график работы в выбранном текущим клубе
     */
    @PostMapping("/exception/{clubId}")
    public ResponseEntity<Void> addWorkScheduleException(
            @PathVariable int clubId,
            @RequestBody @Valid AddWorkScheduleExceptionDto addWorkScheduleExceptionDto
    ) {
        // TODO: Обработка добавления дня-исключения
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление «дня-исключения» в графике работы в выбранном текущим клубе
     */
    @DeleteMapping("/exception/{clubId}/{workScheduleExceptionId}")
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
    @PutMapping("/{clubId}")
    public ResponseEntity<Void> updateWorkSchedule(
            @PathVariable int clubId,
            @RequestBody @Valid UpdateWorkScheduleDto updateWorkScheduleDto
    ) {
        // TODO: Обновление расписания на день недели
        return ResponseEntity.ok().build();
    }
}