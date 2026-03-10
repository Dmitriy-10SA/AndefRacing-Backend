package ru.andef.andefracing.backend.network.controllers.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.profile.employee.EmployeeClubDto;
import ru.andef.andefracing.backend.network.dtos.profile.employee.EmployeePersonalInfoDto;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.PROFILE_EMPLOYEE)
public class EmployeeProfileController {
    /**
     * Получение информации о сотруднике (фамилия, имя, отчество, номер телефона, роли в текущем клубе)
     */
    @GetMapping("/personal-info/{clubId}")
    public ResponseEntity<EmployeePersonalInfoDto> getPersonalInfo(@PathVariable int clubId) {
        // TODO
        return ResponseEntity.ok(null);
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