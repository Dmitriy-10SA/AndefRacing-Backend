package ru.andef.andefracing.backend.network.controllers.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.AuthService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeLoginDto;

import java.util.List;

@Tag(name = "Auth - сотрудник")
@RestController
@RequestMapping(ApiPaths.AUTH_EMPLOYEE)
@Validated
@RequiredArgsConstructor
public class EmployeeAuthController {
    private final AuthService authService;

    /**
     * Проверка, первый ли вход для сотрудника в систему (нужно ли задать пароль)
     */
    @GetMapping(path = "/is-first-enter", version = ApiVersions.V1)
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
     * Подготовительный шаг для входа в систему для сотрудника
     */
    @PostMapping(path = "/pre-login", version = ApiVersions.V1)
    public ResponseEntity<List<EmployeeClubDto>> preLogin(@RequestBody @Valid EmployeeLoginDto loginDto) {
        List<EmployeeClubDto> clubsWhenWork = authService.preLoginEmployee(loginDto);
        return ResponseEntity.ok().body(clubsWhenWork);
    }

    /**
     * Вход в систему для сотрудника
     */
    @PostMapping(path = "/login/{clubId}", version = ApiVersions.V1)
    public ResponseEntity<EmployeeAuthResponseDto> login(
            @PathVariable int clubId,
            @RequestBody @Valid EmployeeLoginDto loginDto
    ) {
        EmployeeAuthResponseDto employeeAuthResponseDto = authService.loginEmployee(clubId, loginDto);
        return ResponseEntity.ok(employeeAuthResponseDto);
    }
}