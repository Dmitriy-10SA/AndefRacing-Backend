package ru.andef.andefracing.backend.network.controllers.club.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.club.management.ClubManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiTags;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.AddWorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.UpdateWorkScheduleDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.WorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.time.LocalDate;
import java.util.List;

@Tag(name = ApiTags.CLUB_MANAGEMENT)
@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_WORK_SCHEDULE)
@Validated
@RequiredArgsConstructor
public class ClubWorkScheduleManagementController {
    private final ClubManagementService clubManagementService;

    /**
     * Добавление «дня-исключения» в график работы в выбранном текущим клубе
     */
    @PostMapping(path = "/exceptions", version = ApiVersions.V1)
    public ResponseEntity<Void> addWorkScheduleExceptionInClub(
            @RequestBody @Valid AddWorkScheduleExceptionDto addWorkScheduleExceptionDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.addWorkScheduleExceptionInClub(principal.clubId(), addWorkScheduleExceptionDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение исключений в расписании на диапазон дат в клубе
     */
    @GetMapping(path = "/exceptions", version = ApiVersions.V1)
    public ResponseEntity<List<WorkScheduleExceptionDto>> getAllWorkSchedulesExceptionsInClub(
            @RequestParam("startDate") @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam("endDate") @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<WorkScheduleExceptionDto> workScheduleExceptions = clubManagementService
                .getAllWorkSchedulesExceptionsInClub(principal.clubId(), startDate, endDate);
        return ResponseEntity.ok(workScheduleExceptions);
    }

    /**
     * Удаление «дня-исключения» в графике работы в выбранном текущим клубе
     */
    @DeleteMapping(path = "/exceptions/{workScheduleExceptionId}", version = ApiVersions.V1)
    public ResponseEntity<Void> deleteWorkScheduleExceptionInClub(
            @PathVariable long workScheduleExceptionId,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.deleteWorkScheduleExceptionInClub(principal.clubId(), workScheduleExceptionId);
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение графика работы, а точнее времени открытия и/или закрытия в конкретный день недели
     */
    @PutMapping(version = ApiVersions.V1)
    public ResponseEntity<Void> updateWorkScheduleInClub(
            @RequestBody @Valid UpdateWorkScheduleDto updateWorkScheduleDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        clubManagementService.updateWorkScheduleInClub(principal.clubId(), updateWorkScheduleDto);
        return ResponseEntity.ok().build();
    }
}