package ru.andef.andefracing.backend.network.controllers.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeLoginDto;

/**
 * Controller для всех Auth действий сотрудника
 */
@RestController
@RequestMapping("/auth/employee")
public class AuthEmployeeController {
    @GetMapping("/is-first-enter")
    public ResponseEntity<Boolean> isFirstEnter(
            @RequestParam(name = "phone")
            @NotBlank(message = "Номер телефона должен быть заполнен")
            @Pattern(
                    regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
                    message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
            )
            String phone
    ) {
        // TODO
        return ResponseEntity.ok(false);
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeAuthResponseDto> login(@RequestBody @Valid EmployeeLoginDto loginDto) {
        // TODO
        return ResponseEntity.ok(new EmployeeAuthResponseDto(""));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<EmployeeAuthResponseDto> changePassword(
            @RequestBody @Valid EmployeeChangePasswordDto changePasswordDto
    ) {
        // TODO ("без СМС, упрощаем, хоть и плохо")
        return ResponseEntity.ok(new EmployeeAuthResponseDto(""));
    }
}