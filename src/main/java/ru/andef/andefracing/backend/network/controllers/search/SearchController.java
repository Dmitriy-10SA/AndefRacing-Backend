package ru.andef.andefracing.backend.network.controllers.search;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andef.andefracing.backend.network.dtos.common.EmployeeAndRolesDto;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;
import ru.andef.andefracing.backend.network.dtos.common.WorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.club.PagedClubShortListDto;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    /**
     * Получение всех регионов
     */
    @GetMapping("/regions")
    public ResponseEntity<List<RegionShortDto>> getAllRegions() {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение всех городов в указанном регионе
     */
    @GetMapping("/cities/{regionId}")
    public ResponseEntity<List<CityShortDto>> getAllCitiesInRegion(@PathVariable short regionId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение всех клубов (работающих) в указанном городе с пагинацией
     */
    @GetMapping("/clubs/{cityId}")
    public ResponseEntity<PagedClubShortListDto> getAllClubsInCity(@PathVariable short cityId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение подробной информации о клубе
     */
    @GetMapping("/club-full-info/{clubId}")
    public ResponseEntity<ClubFullInfoDto> getClubFullInfo(@PathVariable int clubId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение списка сотрудников и их ролей в клубе
     */
    @GetMapping("/hr/employees-and-roles/{clubId}")
    public ResponseEntity<List<EmployeeAndRolesDto>> getEmployeesAndRoles(@PathVariable int clubId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Получение справочника игр (только активных)
     */
    @GetMapping("/games")
    public ResponseEntity<List<GameDto>> getAllGames() {
        // TODO
        return null;
    }

    /**
     * Получение исключений в расписании на неделю
     */
    @GetMapping("/work--schedule-exceptions-for-week")
    public ResponseEntity<List<WorkScheduleExceptionDto>> getAllWorkSchedulesExceptionsForWeek() {
        // TODO
        return null;
    }
}
