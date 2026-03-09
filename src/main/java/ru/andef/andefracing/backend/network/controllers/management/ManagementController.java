package ru.andef.andefracing.backend.network.controllers.management;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;
import ru.andef.andefracing.backend.network.dtos.management.AddPhotoDto;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;
import ru.andef.andefracing.backend.network.dtos.management.AddWorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.dtos.management.UpdateWorkScheduleDto;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/management")
@Validated
public class ManagementController {
    /**
     * Добавление сотрудника в выбранный текущим клуб по номеру телефона сотрудника с заданием ролей
     */
    @PostMapping("/{clubId}")
    public ResponseEntity<Void> addEmployee(
            @PathVariable int clubId,
            @RequestParam(name = "employeePhone")
            @NotBlank(message = "Номер телефона должен быть заполнен")
            @Pattern(
                    regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
                    message = "Телефон должен быть в формате: +7-XXX-XXX-XX-XX"
            )
            String employeePhone,
            @RequestBody List<EmployeeRole> roles
    ) {
        // TODO
        return null;
    }

    /**
     * Удаление сотрудника из выбранного текущим клуба
     */
    @DeleteMapping("/{clubId}/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int clubId, @PathVariable long employeeId) {
        // TODO
        return null;
    }

    /**
     * Добавить роль сотруднику в выбранном текущим клубе
     */
    @PostMapping("/{clubId}/{employeeId}")
    public ResponseEntity<Void> addRoleToEmployee(
            @PathVariable int clubId,
            @PathVariable long employeeId,
            @RequestParam(name = "role") EmployeeRole role
    ) {
        //TODO
        return null;
    }

    /**
     * Изменить роль сотрудника в выбранном текущем клубе
     */
    @PatchMapping("/{clubId}/{employeeId}")
    public ResponseEntity<Void> updateEmployeeRole(
            @PathVariable int clubId,
            @PathVariable long employeeId,
            @RequestParam(name = "oldRole") EmployeeRole oldRole,
            @RequestParam(name = "newRole") EmployeeRole newRole
    ) {
        //TODO
        return null;
    }

    /**
     * Удалить роль сотрудника в выбранном текущим клубе
     */
    @DeleteMapping("/{clubId}/{employeeId}")
    public ResponseEntity<Void> deleteEmployeeRole(
            @PathVariable int clubId,
            @PathVariable long employeeId,
            @RequestParam(name = "role") EmployeeRole role
    ) {
        //TODO
        return null;
    }


    /**
     * Добавление «дня-исключения» в график работы в выбранном текущим клубе
     */
    @PostMapping("/{clubId}/work-schedule-exception")
    public ResponseEntity<Void> addWorkScheduleException(
            @PathVariable int clubId,
            @RequestBody @Valid AddWorkScheduleExceptionDto addWorkScheduleExceptionDto
    ) {
        // TODO: Обработка добавления дня-исключения
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление «дня-исключения» в графике работы в выбранном текущим клубе
     */
    @DeleteMapping("/work-schedule-exception/{workScheduleExceptionId}")
    public ResponseEntity<Void> deleteWorkScheduleException(@PathVariable long workScheduleExceptionId) {
        // TODO: Обработка удаления дня-исключения
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение графика работы, а точнее времени открытия и/или закрытия в конкретный день недели
     */
    @PatchMapping("/{clubId}/work-schedule")
    public ResponseEntity<Void> updateWorkSchedule(
            @PathVariable int clubId,
            @RequestBody @Valid UpdateWorkScheduleDto updateWorkScheduleDto
    ) {
        // TODO: Обновление расписания на день недели
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение количества симуляторов в выбранном текущим клубе
     */
    @PatchMapping("/{clubId}")
    public ResponseEntity<Void> updateCntEquipmentInClub(
            @PathVariable int clubId,
            @RequestParam(name = "cntEquipment") @Min(1) int cntEquipment
    ) {
        // TODO: Проверить, что count >= 0, и обновить количество симуляторов
        return ResponseEntity.ok().build();
    }


    /**
     * Добавить активную игру в клуб (из справочника)
     */
    @PostMapping("/{clubId}/game")
    public ResponseEntity<Void> addGameToClub(
            @PathVariable int clubId,
            @RequestBody @Valid GameDto gameDto
    ) {
        // TODO: Добавить игру в список активных игр клуба
        return ResponseEntity.ok().build();
    }

    /**
     * Удалить игру из клуба
     */
    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<Void> removeGameFromClub(@PathVariable short gameId) {
        // TODO: Удалить игру из списка активных игр клуба
        return ResponseEntity.ok().build();
    }


    /**
     * Добавление цены за кол-во минут игры
     */
    @PostMapping("/{clubId}/price")
    public ResponseEntity<Void> addPriceForMinutes(
            @PathVariable int clubId,
            @RequestBody @Valid AddPriceDto addPriceDto
    ) {
        // TODO: Добавить цену за указанные минуты
        return ResponseEntity.ok().build();
    }

    /**
     * Изменение цены за кол-во минут игры
     */
    @PatchMapping("/price/{priceId}")
    public ResponseEntity<Void> updatePriceForMinutes(
            @PathVariable long priceId,
            @RequestParam(name = "value") @NotNull @Min(1) BigDecimal value
    ) {
        // TODO: Обновить существующую цену за минуты
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление цены за кол-во минут игры
     */
    @DeleteMapping("/price/{priceId}")
    public ResponseEntity<Void> deletePriceForMinutes(@PathVariable long priceId) {
        // TODO: Удалить цену за указанное количество минут
        return ResponseEntity.ok().build();
    }


    /**
     * Добавление фотографии
     */
    @PostMapping("/{clubId}/photo")
    public ResponseEntity<Void> addPhoto(
            @PathVariable int clubId,
            @RequestBody @Valid AddPhotoDto addPhotoDto
    ) {
        // TODO: Добавить URL фотографии в галерею клуба
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление фотографии
     */
    @DeleteMapping("/photo/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable long photoId) {
        // TODO: Удалить фотографию из галереи клуба
        return ResponseEntity.ok().build();
    }

    /**
     * Переупорядочивание фотографий
     */
    @PatchMapping("/{clubId}/photo/reorder")
    public ResponseEntity<Void> reorderPhotos(
            @PathVariable int clubId,
            @RequestBody @Valid @NotNull List<@NotNull Long> orderedPhotoIds
    ) {
        // TODO: Обновить порядок фотографий в галерее
        return ResponseEntity.ok().build();
    }


    /**
     * Открыть клуб
     */
    @PatchMapping("/{clubId}/open")
    public ResponseEntity<Void> openClub(@PathVariable int clubId) {
        // TODO: Установить статус клуба как "открыт"
        return ResponseEntity.ok().build();
    }

    /**
     * Закрыть клуб
     */
    @PatchMapping("/{clubId}/close")
    public ResponseEntity<Void> closeClub(@PathVariable int clubId) {
        // TODO: Установить статус клуба как "закрыт"
        return ResponseEntity.ok().build();
    }
}
