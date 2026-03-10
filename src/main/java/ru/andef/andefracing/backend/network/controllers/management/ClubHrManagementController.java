package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.management.hr.EmployeeAndRolesDto;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CLUB_MANAGEMENT_HR)
@Validated
public class ClubHrManagementController {
    /**
     * Добавление сотрудника в выбранный текущим клуб по номеру телефона сотрудника с заданием ролей
     */
    @PostMapping("/add-employee")
    public ResponseEntity<Void> addEmployee(
            @PathVariable int clubId,
            @RequestParam(name = "employeePhone")
            @NotBlank(message = "Номер телефона должен быть заполнен")
            @Pattern(
                    regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
                    message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
            )
            String employeePhone,
            @RequestBody List<@NotNull EmployeeRole> roles
    ) {
        // TODO
        return null;
    }

    /**
     * Получение списка сотрудников и их ролей в клубе
     */
    @GetMapping
    public ResponseEntity<List<EmployeeAndRolesDto>> getEmployeesAndRoles(@PathVariable int clubId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Удаление сотрудника из выбранного текущим клуба
     */
    @DeleteMapping("/delete-employee/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int clubId, @PathVariable long employeeId) {
        // TODO
        return null;
    }

    /**
     * Добавить роль сотруднику в выбранном текущим клубе
     */
    @PostMapping("/add-role-to-employee/{employeeId}")
    public ResponseEntity<Void> addRoleToEmployee(
            @PathVariable int clubId,
            @PathVariable long employeeId,
            @RequestParam(name = "role") @NotNull EmployeeRole role
    ) {
        //TODO
        return null;
    }

    /**
     * Изменить роль сотрудника в выбранном текущем клубе
     */
    @PatchMapping("/update-employee-role/{employeeId}")
    public ResponseEntity<Void> updateEmployeeRole(
            @PathVariable int clubId,
            @PathVariable long employeeId,
            @RequestParam(name = "oldRole") @NotNull EmployeeRole oldRole,
            @RequestParam(name = "newRole") @NotNull EmployeeRole newRole
    ) {
        //TODO
        return null;
    }

    /**
     * Удалить роль сотрудника в выбранном текущим клубе
     */
    @DeleteMapping("/delete-employee-role/{employeeId}")
    public ResponseEntity<Void> deleteEmployeeRole(
            @PathVariable int clubId,
            @PathVariable long employeeId,
            @RequestParam(name = "role") @NotNull EmployeeRole role
    ) {
        //TODO
        return null;
    }
}