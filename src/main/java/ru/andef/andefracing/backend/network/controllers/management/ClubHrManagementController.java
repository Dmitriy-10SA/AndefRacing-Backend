package ru.andef.andefracing.backend.network.controllers.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.domain.services.ManagementService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.management.hr.AddExistingEmployeeDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.AddNewEmployeeDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.EmployeeAndRolesDto;
import ru.andef.andefracing.backend.network.security.JwtFilter;

import java.util.List;

@Tag(name = "Management - персонал")
@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_HR)
@Validated
@RequiredArgsConstructor
public class ClubHrManagementController {
    private final ManagementService managementService;

    /**
     * Проверка, что сотрудник есть в системе
     */
    @GetMapping(path = "is-employee-in-system", version = ApiVersions.V1)
    public ResponseEntity<Boolean> isEmployeeInSystem(
            @RequestParam(name = "employeePhone")
            @NotBlank(message = "Номер телефона должен быть заполнен")
            @Pattern(
                    regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
                    message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
            )
            String employeePhone
    ) {
        boolean isInSystem = managementService.isEmployeeInSystem(employeePhone);
        return ResponseEntity.ok(isInSystem);
    }

    /**
     * Добавление сотрудника, которого нет в системе, в выбранный текущим клуб
     * по номеру телефона сотрудника с заданием ролей
     */
    @PostMapping(path = "/add-new-employee-to-club", version = ApiVersions.V1)
    public ResponseEntity<Void> addNewEmployeeToClub(
            @RequestBody @Valid AddNewEmployeeDto addNewEmployeeDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.addNewEmployeeToClub(principal.clubId(), addNewEmployeeDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Добавление сотрудника, который уже есть в системе, в выбранный текущим клуб
     * по номеру телефона сотрудника с заданием ролей
     */
    @PostMapping(path = "/add-existing-employee-to-club", version = ApiVersions.V1)
    public ResponseEntity<Void> addExistingEmployeeToClub(
            @RequestBody @Valid AddExistingEmployeeDto addExistingEmployeeDto,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.addExistingEmployeeToClub(principal.clubId(), addExistingEmployeeDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение списка сотрудников и их ролей в клубе
     */
    @GetMapping(version = ApiVersions.V1)
    public ResponseEntity<List<EmployeeAndRolesDto>> getEmployeesAndRolesInClub(Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<EmployeeAndRolesDto> employeeAndRoles = managementService.getEmployeesAndRolesInClub(principal.clubId());
        return ResponseEntity.ok(employeeAndRoles);
    }

    /**
     * Удаление сотрудника из выбранного текущим клуба
     */
    @DeleteMapping(path = "/delete-employee-from-club/{employeeId}", version = ApiVersions.V1)
    public ResponseEntity<Void> deleteEmployeeFromClub(@PathVariable long employeeId, Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.deleteEmployeeFromClub(principal.clubId(), employeeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Добавить роль сотруднику в выбранном текущим клубе
     */
    @PostMapping(path = "/add-role-to-employee-in-club/{employeeId}", version = ApiVersions.V1)
    public ResponseEntity<Void> addRoleToEmployeeInClub(
            @PathVariable long employeeId,
            @RequestParam(name = "role") @NotNull EmployeeRole role,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.addRoleToEmployeeInClub(principal.clubId(), employeeId, role);
        return ResponseEntity.ok().build();
    }

    /**
     * Изменить роль сотрудника в выбранном текущем клубе
     */
    @PatchMapping(path = "/update-employee-role-in-club/{employeeId}", version = ApiVersions.V1)
    public ResponseEntity<Void> updateEmployeeRoleInClub(
            @PathVariable long employeeId,
            @RequestParam(name = "oldRole") @NotNull EmployeeRole oldRole,
            @RequestParam(name = "newRole") @NotNull EmployeeRole newRole,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.updateEmployeeRoleInClub(principal.clubId(), employeeId, oldRole, newRole);
        return ResponseEntity.ok().build();
    }

    /**
     * Удалить роль сотрудника в выбранном текущим клубе
     */
    @DeleteMapping(path = "/delete-employee-role-in-club/{employeeId}", version = ApiVersions.V1)
    public ResponseEntity<Void> deleteEmployeeRoleInClub(
            @PathVariable long employeeId,
            @RequestParam(name = "role") @NotNull EmployeeRole role,
            Authentication authentication
    ) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        managementService.deleteEmployeeRoleInClub(principal.clubId(), employeeId, role);
        return ResponseEntity.ok().build();
    }
}