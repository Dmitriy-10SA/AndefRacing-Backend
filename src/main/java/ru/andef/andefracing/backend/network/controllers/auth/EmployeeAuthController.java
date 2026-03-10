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
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;

import java.util.List;

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
    @PostMapping("/login/{clubId}")
    public ResponseEntity<EmployeeAuthResponseDto> login(
            @PathVariable int clubId,
            @RequestBody @Valid EmployeeLoginDto loginDto
    ) {
        EmployeeAuthResponseDto employeeAuthResponseDto = authService.loginEmployee(clubId, loginDto);
        return ResponseEntity.ok(employeeAuthResponseDto);
    }

    /**
     * Смена пароля у сотрудника по номеру телефона
     */
    @PatchMapping("/change-password/{clubId}")
    public ResponseEntity<EmployeeAuthResponseDto> changePassword(
            @PathVariable int clubId,
            @RequestBody @Valid EmployeeChangePasswordDto changePasswordDto
    ) {
        EmployeeAuthResponseDto employeeAuthResponseDto = authService
                .changePasswordEmployee(clubId, changePasswordDto);
        return ResponseEntity.ok(employeeAuthResponseDto);
    }

    /**
     * Получение списка всех клубов, где работает сотрудник
     */
    @GetMapping("/clubs")
    public ResponseEntity<List<EmployeeClubDto>> getAllClubs() {
        // TODO
        return null;
    }
}