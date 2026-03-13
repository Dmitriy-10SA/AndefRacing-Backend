package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeClub;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.data.repositories.club.*;
import ru.andef.andefracing.backend.domain.exceptions.DuplicateException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.management.*;
import ru.andef.andefracing.backend.domain.mappers.club.*;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;
import ru.andef.andefracing.backend.network.dtos.management.AddPhotoDto;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.AddExistingEmployeeDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.AddNewEmployeeDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.EmployeeAndRolesDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.AddWorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.UpdateWorkScheduleDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.WorkScheduleExceptionDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ManagementService {
    private final SService searchService;

    private final ClubRepository clubRepository;
    private final GameRepository gameRepository;
    private final EmployeeRepository employeeRepository;
    private final BookingRepository bookingRepository;
    private final PriceRepository priceRepository;
    private final WorkScheduleExceptionRepository workScheduleExceptionRepository;

    private final GameMapper gameMapper;
    private final EmployeeMapper employeeMapper;
    private final PhotoMapper photoMapper;
    private final PriceMapper priceMapper;
    private final WorkScheduleExceptionMapper workScheduleExceptionMapper;

    /**
     * Валидация графика работы
     */
    private void validateWorkSchedule(boolean isWorkDay, LocalTime openTime, LocalTime closeTime) {
        boolean isValidWorkDay = isWorkDay && openTime != null && closeTime != null;
        boolean isValidWeekend = !isWorkDay && openTime == null && closeTime == null;
        boolean isValidWorkSchedule = isValidWorkDay || isValidWeekend;
        if (!isValidWorkSchedule) {
            throw new InvalidWorkScheduleException(
                    "Неверные данные, если это рабочий день, то нужно передать время открытия и закрытия," +
                            " а если выходной - то время открытия и закрытия null"
            );
        }
        if (isWorkDay && !openTime.isBefore(closeTime)) {
            throw new InvalidWorkScheduleException("Неверные данные, время открытия позже времени закрытия");
        }
    }

    /**
     * Получение ролей сотрудника в клубе (вспомогательный метод)
     */
    private List<EmployeeRole> findEmployeeRolesInClub(Club club, Employee employee) {
        List<EmployeeRole> rolesInClub = new ArrayList<>();
        for (EmployeeClub employeeClub : club.getEmployeesAndRoles()) {
            if (employeeClub.getEmployee().equals(employee)) {
                rolesInClub.add(employeeClub.getEmployeeRole());
            }
        }
        return rolesInClub;
    }

    /**
     * Удаление роли у сотрудника в клубе (вспомогательный метод)
     */
    private void deleteEmployeeRoleInClub(Club club, Employee employee, EmployeeRole role) {
        List<EmployeeRole> rolesInClub = findEmployeeRolesInClub(club, employee);
        if (rolesInClub.isEmpty()) {
            throw new EntityNotFoundException("Сотрудник c id " + employee.getId() + " не найден в клубе");
        } else if (!rolesInClub.contains(role)) {
            throw new EntityNotFoundException("У сотрудника с id " + employee.getId() + " нет роли " + role.getRu());
        }
        club.deleteRoleForEmployee(employee, role);
    }

    /**
     * Добавить активную игру в клуб (из справочника)
     */
    @Transactional
    public void addGameToClub(int clubId, short gameId) {
        Club club = searchService.findClubById(clubId);
        Game game = searchService.findGameById(gameId);
        List<Game> gamesInClub = gameRepository.findAllActiveGamesInClub(club.getId());
        if (gamesInClub.contains(game)) {
            throw new DuplicateException("Игра с id " + gameId + " уже есть в клубе");
        }
        club.addGame(game);
        clubRepository.save(club);
    }

    /**
     * Получение справочника игр (только активных)
     */
    @Transactional(readOnly = true)
    public List<GameDto> getAllActiveGames() {
        List<Game> games = gameRepository.findAllByIsActiveTrue();
        return gameMapper.toDto(games);
    }

    /**
     * Удалить игру из клуба
     */
    @Transactional
    public void deleteGameInClub(int clubId, short gameId) {
        Club club = searchService.findClubById(clubId);
        Game game = searchService.findGameById(gameId);
        List<Game> gamesInClub = gameRepository.findAllActiveGamesInClub(club.getId());
        if (!gamesInClub.contains(game)) {
            throw new EntityNotFoundException("Игра с id " + gameId + " не найдена в клубе");
        }
        club.deleteGame(game);
        clubRepository.save(club);
    }

    /**
     * Проверка, что сотрудник есть в системе
     */
    @Transactional(readOnly = true)
    public boolean isEmployeeInSystem(String phone) {
        return employeeRepository.findByPhone(phone).isPresent();
    }

    /**
     * Добавление сотрудника, которого нет в системе, в выбранный текущим клуб
     * по номеру телефона сотрудника с заданием ролей
     */
    @Transactional
    public void addNewEmployeeToClub(int clubId, AddNewEmployeeDto addNewEmployeeDto) {
        Club club = searchService.findClubById(clubId);
        try {
            searchService.findEmployeeByPhone(addNewEmployeeDto.getPhone());
            throw new EmployeeWithThisPhoneAlreadyExistsException(addNewEmployeeDto.getPhone());
        } catch (EntityNotFoundException e) {
            Employee employee = new Employee(
                    addNewEmployeeDto.getSurname(),
                    addNewEmployeeDto.getName(),
                    addNewEmployeeDto.getPatronymic(),
                    addNewEmployeeDto.getPhone()
            );
            employee = employeeRepository.save(employee);
            club.addEmployee(employee, addNewEmployeeDto.getRoles());
            clubRepository.save(club);
        }
    }

    /**
     * Добавление сотрудника, который уже есть в системе, в выбранный текущим клуб
     * по номеру телефона сотрудника с заданием ролей
     */
    @Transactional
    public void addExistingEmployeeToClub(int clubId, AddExistingEmployeeDto addExistingEmployeeDto) {
        Club club = searchService.findClubById(clubId);
        Employee employee = searchService.findEmployeeByPhone(addExistingEmployeeDto.getPhone());
        for (EmployeeClub employeeClub : club.getEmployeesAndRoles()) {
            if (employeeClub.getEmployee().equals(employee)) {
                throw new DuplicateException("Сотрудник с id " + employee.getId() + " уже есть в клубе");
            }
        }
        club.addEmployee(employee, addExistingEmployeeDto.getRoles());
        clubRepository.save(club);
    }

    /**
     * Получение списка сотрудников и их ролей в клубе
     */
    @Transactional(readOnly = true)
    public List<EmployeeAndRolesDto> getEmployeesAndRolesInClub(int clubId) {
        Club club = searchService.findClubById(clubId);
        Map<Employee, List<EmployeeRole>> employeeAndRolesMap = new HashMap<>();
        for (EmployeeClub employeeClub : club.getEmployeesAndRoles()) {
            if (employeeAndRolesMap.containsKey(employeeClub.getEmployee())) {
                employeeAndRolesMap.put(employeeClub.getEmployee(), new ArrayList<>());
            }
            employeeAndRolesMap.get(employeeClub.getEmployee()).add(employeeClub.getEmployeeRole());
        }
        List<EmployeeAndRolesDto> employeeAndRoles = new ArrayList<>();
        for (Map.Entry<Employee, List<EmployeeRole>> entry : employeeAndRolesMap.entrySet()) {
            employeeAndRoles.add(new EmployeeAndRolesDto(employeeMapper.toDto(entry.getKey()), entry.getValue()));
        }
        return employeeAndRoles;
    }

    /**
     * Удаление сотрудника из выбранного текущим клуба
     */
    @Transactional
    public void deleteEmployeeFromClub(int clubId, long employeeId) {
        Club club = searchService.findClubById(clubId);
        Employee employee = searchService.findEmployeeById(employeeId);
        boolean isExistingInClub = false;
        for (EmployeeClub employeeClub : club.getEmployeesAndRoles()) {
            if (employeeClub.getEmployee().equals(employee)) {
                isExistingInClub = true;
                break;
            }
        }
        if (!isExistingInClub) {
            throw new EntityNotFoundException("Сотрудник c id " + employeeId + " не найден в клубе");
        }
        club.deleteEmployee(employee);
        clubRepository.save(club);
    }

    /**
     * Добавить роль сотруднику в выбранном текущим клубе
     */
    @Transactional
    public void addRoleToEmployeeInClub(int clubId, long employeeId, EmployeeRole role) {
        Club club = searchService.findClubById(clubId);
        Employee employee = searchService.findEmployeeById(employeeId);
        List<EmployeeRole> rolesInClub = findEmployeeRolesInClub(club, employee);
        if (rolesInClub.isEmpty()) {
            throw new EntityNotFoundException("Сотрудник c id " + employeeId + " не найден в клубе");
        } else if (rolesInClub.contains(role)) {
            throw new DuplicateException("Сотрудник с id " + employeeId + " уже имеет роль " + role.getRu());
        }
        club.addRoleForEmployee(employee, role);
        clubRepository.save(club);
    }

    /**
     * Изменить роль сотрудника в выбранном текущем клубе
     */
    @Transactional
    public void updateEmployeeRoleInClub(int clubId, long employeeId, EmployeeRole oldRole, EmployeeRole newRole) {
        Club club = searchService.findClubById(clubId);
        Employee employee = searchService.findEmployeeById(employeeId);
        deleteEmployeeRoleInClub(club, employee, oldRole);
        club.addRoleForEmployee(employee, newRole);
        clubRepository.save(club);
    }

    /**
     * Удалить роль сотрудника в выбранном текущим клубе
     */
    @Transactional
    public void deleteEmployeeRoleInClub(int clubId, long employeeId, EmployeeRole role) {
        Club club = searchService.findClubById(clubId);
        Employee employee = searchService.findEmployeeById(employeeId);
        deleteEmployeeRoleInClub(club, employee, role);
        clubRepository.save(club);
    }

    /**
     * Изменение количества симуляторов в выбранном текущим клубе
     */
    @Transactional
    public void updateCntEquipmentInClub(int clubId, short cntEquipment) {
        Club club = searchService.findClubById(clubId);
        club.setCntEquipment(cntEquipment);
        clubRepository.save(club);
    }

    /**
     * Открыть клуб
     */
    @Transactional
    public void openClub(int clubId) {
        Club club = searchService.findClubById(clubId);
        int photosCnt = club.getPhotos().size();
        int pricesCnt = club.getPrices().size();
        int workSchedulesCnt = club.getWorkSchedules().size();
        int activeGamesCnt = gameRepository.findAllActiveGamesInClub(club.getId()).size();
        if (photosCnt >= 1 && pricesCnt >= 1 && workSchedulesCnt == 7 && activeGamesCnt >= 1) {
            club.setOpen(true);
            clubRepository.save(club);
        } else {
            throw new ClubOpenConditionsNotMetException(clubId);
        }
    }

    /**
     * Закрыть клуб
     */
    @Transactional
    public void closeClub(int clubId) {
        Club club = searchService.findClubById(clubId);
        long countUpcomingPaidOrPendingBookings = bookingRepository.countUpcomingPaidOrPendingBookings(club.getId());
        if (countUpcomingPaidOrPendingBookings <= 0) {
            club.setOpen(false);
            clubRepository.save(club);
        } else {
            throw new ClubCloseConditionsNotMetException(clubId);
        }
    }

    /**
     * Добавление фотографии в клуб
     */
    @Transactional
    public void addPhotoInClub(int clubId, AddPhotoDto addPhotoDto) {
        Club club = searchService.findClubById(clubId);
        List<Photo> photosInClub = club.getPhotos();
        List<String> urls = photosInClub.stream().map(Photo::getUrl).toList();
        List<Short> sequenceNumbers = photosInClub.stream().map(Photo::getSequenceNumber).toList();
        if (urls.contains(addPhotoDto.url())) {
            throw new DuplicateException("Фото с url" + addPhotoDto.url() + " уже существует в клубе");
        } else if (sequenceNumbers.contains(addPhotoDto.sequenceNumber())) {
            throw new DuplicateException(
                    "Фото с sequenceNumber" + addPhotoDto.sequenceNumber() + " уже существует в клубе"
            );
        }
        Photo photo = photoMapper.toEntity(addPhotoDto);
        club.addPhoto(photo);
        clubRepository.save(club);
    }

    /**
     * Удаление фотографии из клуба
     */
    @Transactional
    public void deletePhotoFromClub(int clubId, long photoId) {
        Club club = searchService.findClubById(clubId);
        Photo photo = searchService.findPhotoById(photoId);
        boolean isDeleted = club.deletePhoto(photo);
        if (isDeleted) {
            clubRepository.save(club);
        } else {
            throw new EntityNotFoundException("Фото с id " + photoId + " не найдено в клубе");
        }
    }

    /**
     * Переупорядочивание фотографий в клубе
     */
    @Transactional
    public void reorderPhotosInClub(int clubId, List<Long> orderedPhotoIds) {
        Club club = searchService.findClubById(clubId);
        List<Photo> photosInClub = club.getPhotos();
        if (photosInClub.size() != orderedPhotoIds.size()) {
            throw new PhotoReorderMismatchException(
                    "Несовпадение кол-ва фотографий в клубе (" + photosInClub.size() +
                            ") и для переупорядочивания (" + orderedPhotoIds.size() + ")"
            );
        }
        photosInClub.forEach(photo -> {
            if (!orderedPhotoIds.contains(photo.getId())) {
                throw new PhotoReorderMismatchException(
                        "Фото с id " + photo.getId() + " не указано, хотя должно быть"
                );
            }
        });
        club.reorderPhotos(orderedPhotoIds);
        clubRepository.save(club);
    }

    /**
     * Добавление цены за кол-во минут игры в клубе
     */
    @Transactional
    public void addPriceForMinutesInClub(int clubId, AddPriceDto addPriceDto) {
        Club club = searchService.findClubById(clubId);
        List<Price> pricesInClub = club.getPrices();
        List<Short> durationMinutes = pricesInClub.stream().map(Price::getDurationMinutes).toList();
        if (durationMinutes.contains(addPriceDto.durationMinutes())) {
            throw new DuplicateException(
                    "В клубе с id " + clubId + " уже есть стоимость за " + addPriceDto.durationMinutes() + " минут"
            );
        }
        Price price = priceMapper.toEntity(addPriceDto);
        price.setValue(price.getValue().setScale(2, RoundingMode.HALF_EVEN));
        club.addPrice(price);
        clubRepository.save(club);
    }

    /**
     * Изменение цены за кол-во минут игры в клубе
     */
    @Transactional
    public void updatePriceForMinutesInClub(int clubId, long priceId, BigDecimal value) {
        Club club = searchService.findClubById(clubId);
        for (Price price : club.getPrices()) {
            if (price.getId() == priceId) {
                price.setValue(value.setScale(2, RoundingMode.HALF_EVEN));
                priceRepository.save(price);
                return;
            }
        }
        throw new EntityNotFoundException("Цена с id " + priceId + " не найдена в клубе");
    }

    /**
     * Удаление цены за кол-во минут игры в клубе
     */
    @Transactional
    public void deletePriceForMinutesInClub(int clubId, long priceId) {
        Club club = searchService.findClubById(clubId);
        Price price = searchService.findPriceById(priceId);
        boolean isDeleted = club.deletePrice(price);
        if (isDeleted) {
            clubRepository.save(club);
        } else {
            throw new EntityNotFoundException("Цена с id " + priceId + " не найдена в клубе");
        }
    }

    /**
     * Добавление «дня-исключения» в график работы в выбранном текущим клубе
     */
    @Transactional
    public void addWorkScheduleExceptionInClub(int clubId, AddWorkScheduleExceptionDto addWorkScheduleExceptionDto) {
        boolean isWorkDay = addWorkScheduleExceptionDto.isWorkDay();
        LocalDate date = addWorkScheduleExceptionDto.date();
        LocalTime openTime = addWorkScheduleExceptionDto.openTime();
        LocalTime closeTime = addWorkScheduleExceptionDto.closeTime();
        String description = addWorkScheduleExceptionDto.description();
        validateWorkSchedule(isWorkDay, openTime, closeTime);
        Club club = searchService.findClubById(clubId);
        if (workScheduleExceptionRepository.findByClubIdAndDate(club.getId(), date).isPresent()) {
            throw new DuplicateException("День-исключение с датой " + date + " уже есть в клубе");
        }
        WorkScheduleException workScheduleException;
        if (isWorkDay) {
            workScheduleException = new WorkScheduleException(date, openTime, closeTime, description);
        } else {
            workScheduleException = new WorkScheduleException(date, description);
        }
        club.addWorkScheduleException(workScheduleException);
        clubRepository.save(club);
    }

    /**
     * Получение исключений в расписании на диапазон дат в клубе
     */
    @Transactional(readOnly = true)
    public List<WorkScheduleExceptionDto> getAllWorkSchedulesExceptionsInClub(
            int clubId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Club club = searchService.findClubById(clubId);
        List<WorkScheduleException> workScheduleExceptions = workScheduleExceptionRepository.
                findAllByRangeOfDatesBetweenStartAndEnd(club.getId(), startDate, endDate);
        return workScheduleExceptionMapper.toDto(workScheduleExceptions);
    }

    /**
     * Удаление «дня-исключения» в графике работы в выбранном текущим клубе
     */
    @Transactional
    public void deleteWorkScheduleExceptionInClub(int clubId, long workScheduleExceptionId) {
        Club club = searchService.findClubById(clubId);
        WorkScheduleException workScheduleException = workScheduleExceptionRepository
                .findByIdAndClubId(workScheduleExceptionId, club.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "День-исключение с id " + workScheduleExceptionId + " не найден в клубе"
                        )
                );
        club.deleteWorkScheduleException(workScheduleException);
        clubRepository.save(club);
    }

    /**
     * Изменение графика работы, а точнее времени открытия и/или закрытия в конкретный день недели
     */
    @Transactional
    public void updateWorkScheduleInClub(int clubId, UpdateWorkScheduleDto updateWorkScheduleDto) {
        boolean isWorkDay = updateWorkScheduleDto.isWorkDay();
        DayOfWeek dayOfWeek = updateWorkScheduleDto.dayOfWeek();
        LocalTime openTime = updateWorkScheduleDto.openTime();
        LocalTime closeTime = updateWorkScheduleDto.closeTime();
        validateWorkSchedule(isWorkDay, openTime, closeTime);
        Club club = searchService.findClubById(clubId);
        if (isWorkDay) {
            club.updateDayFromWorkScheduleToWorkingDay(dayOfWeek, openTime, closeTime);
        } else {
            club.updateDayFromWorkScheduleToNonWorkingDay(dayOfWeek);
        }
        clubRepository.save(club);
    }
}