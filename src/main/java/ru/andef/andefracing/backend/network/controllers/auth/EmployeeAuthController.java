package ru.andef.andefracing.backend.network.controllers.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.AuthService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeLoginDto;

@RestController
@RequestMapping(ApiPaths.AUTH_EMPLOYEE)
@Validated
@RequiredArgsConstructor
public class EmployeeAuthController {
    private final AuthService authService;

    /**
     * Проверка, первый ли вход для сотрудника в систему (нужно ли задать пароль)
     */
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
        boolean isFirstEnter = authService.isEmployeeFirstEnter(phone);
        return ResponseEntity.ok(isFirstEnter);
    }

    /**
     * Вход в систему для сотрудника
     */
    @PostMapping("/login")
    public ResponseEntity<EmployeeAuthResponseDto> login(@RequestBody @Valid EmployeeLoginDto loginDto) {
        // TODO
        return ResponseEntity.ok(new EmployeeAuthResponseDto(""));
    }

    /**
     * Смена пароля у сотрудника по номеру телефона
     */
    @PatchMapping("/change-password")
    public ResponseEntity<EmployeeAuthResponseDto> changePassword(
            @RequestBody @Valid EmployeeChangePasswordDto changePasswordDto
    ) {
        // TODO ("без СМС, упрощаем, хоть и плохо")
        return ResponseEntity.ok(new EmployeeAuthResponseDto(""));
    }
}