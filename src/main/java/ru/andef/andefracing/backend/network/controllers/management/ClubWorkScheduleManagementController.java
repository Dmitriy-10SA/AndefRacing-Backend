package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.ManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.AddWorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.UpdateWorkScheduleDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.WorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.security.JwtFilter;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_WORK_SCHEDULE)
@Validated
@RequiredArgsConstructor
public class ClubWorkScheduleManagementController {
    private final ManagementService managementService;

    /**
     * Добавление «дня-исключения» в график работы в выбранном текущим клубе
     */
    @PostMapping("/exceptions")
    public ResponseEntity<Void> addWorkScheduleExceptionInClub(
            @RequestBody @Valid AddWorkScheduleExceptionDto addWorkScheduleExceptionDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.addWorkScheduleExceptionInClub(principal.clubId(), addWorkScheduleExceptionDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение исключений в расписании на диапазон дат в клубе
     */
    @GetMapping("/exceptions")
    public ResponseEntity<List<WorkScheduleExceptionDto>> getAllWorkSchedulesExceptionsInClub(
            @RequestParam("startDate") @NotNull LocalDate startDate,
            @RequestParam("endDate") @NotNull LocalDate endDate,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<WorkScheduleExceptionDto> workScheduleExceptions = managementService
                .getAllWorkSchedulesExceptionsInClub(principal.clubId(), startDate, endDate);
        return ResponseEntity.ok(workScheduleExceptions);
    }

    /**
     * Удаление «дня-исключения» в графике работы в выбранном текущим клубе
     */
    @DeleteMapping("/exceptions/{workScheduleExceptionId}")
    public ResponseEntity<Void> deleteWorkScheduleExceptionInClub(
            @PathVariable long workScheduleExceptionId,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.deleteWorkScheduleExceptionInClub(principal.clubId(), workScheduleExceptionId);
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение графика работы, а точнее времени открытия и/или закрытия в конкретный день недели
     */
    @PutMapping
    public ResponseEntity<Void> updateWorkScheduleInClub(
            @RequestBody @Valid UpdateWorkScheduleDto updateWorkScheduleDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.updateWorkScheduleInClub(principal.clubId(), updateWorkScheduleDto);
        return ResponseEntity.ok().build();
    }
}