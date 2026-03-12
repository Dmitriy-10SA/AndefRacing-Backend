package ru.andef.andefracing.backend.network.controllers.profile;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.domain.services.ProfileService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.profile.employee.EmployeePersonalInfoDto;
import ru.andef.andefracing.backend.network.security.JwtFilter;

@Tag(name = "Profile - сотрудник")
@RestController
@RequestMapping(ApiPaths.PROFILE_EMPLOYEE)
@RequiredArgsConstructor
public class EmployeeProfileController {
    private final ProfileService profileService;

    /**
     * Получение информации о сотруднике (фамилия, имя, отчество, номер телефона, роли в текущем клубе)
     */
    @GetMapping(path = "/personal-info", version = ApiVersions.V1)
    public ResponseEntity<EmployeePersonalInfoDto> getPersonalInfo(Authentication authentication) {
        JwtFilter.EmployeePrincipal principal = (JwtFilter.EmployeePrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        EmployeePersonalInfoDto employeePersonalInfoDto = profileService
                .getEmployeePersonalInfo(principal.id(), principal.clubId());
        return ResponseEntity.ok(employeePersonalInfoDto);
    }
}