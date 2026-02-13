package ru.andef.andefracing.backend.data.entities.club;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.andef.andefracing.backend.data.entities.club.game.Game;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.entities.club.photo.Photo;
import ru.andef.andefracing.backend.data.entities.club.price.Price;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.data.entities.location.City;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сущности Club
 *
 * @see Club
 */
class ClubTest {
    /**
     * Возврат клуба для тестов
     */
    private Club getClub() {
        return new Club(
                1,
                new City(),
                "name",
                "phone",
                "email",
                "address",
                (short) 1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    /**
     * Возврат сотрудника для тестов
     */
    private Employee getEmployee() {
        return getEmployeeWithId(1);
    }

    /**
     * Возврат сотрудника для тестов с id
     */
    private Employee getEmployeeWithId(long id) {
        return new Employee(
                id,
                "surname",
                "name",
                null,
                "+7-999-999-99-99",
                "password",
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    /**
     * Возврат игры для тестов
     */
    private Game getGame(short id) {
        return new Game(id, "name", "url", true);
    }

    /**
     * Добавление в клуб графика работы со всеми нерабочими днями
     */
    private void initWorkScheduleInClubByNotWorkingDays(Club club) {
        for (int i = 1; i < 8; i++) {
            club.getWorkSchedules().add(new WorkSchedule(i, (short) i, null, null, false));
        }
    }

    /**
     * Добавление в клуб графика работы со всеми рабочими днями
     */
    private void initWorkScheduleInClubByWorkingDays(Club club) {
        LocalTime openTime = LocalTime.of(10, 20);
        LocalTime closeTime = LocalTime.of(20, 10);
        for (int i = 1; i < 8; i++) {
            club.getWorkSchedules().add(new WorkSchedule(i, (short) i, openTime, closeTime, true));
        }
    }

    @Test
    @DisplayName("Добавление сотрудника в клуб")
    void testAddEmployeeToClub() {
        Club club = getClub();
        assertTrue(club.getEmployeesAndRoles().isEmpty());
        Employee employee = getEmployee();
        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(new EmployeeRole((short) 1, "ADMIN"));
        employeeRoles.add(new EmployeeRole((short) 2, "OWNER"));
        club.addEmployee(employee, employeeRoles);
        assertEquals(2, club.getEmployeesAndRoles().size());
        club.getEmployeesAndRoles().forEach(employeeClub -> {
            assertEquals(club, employeeClub.getClub());
            assertEquals(employee, employeeClub.getEmployee());
            assertTrue(employeeRoles.contains(employeeClub.getEmployeeRole()));
        });
    }

    @Test
    @DisplayName("Добавление роли сотруднику в клубе")
    void testAddRoleForEmployeeInClub() {
        Club club = getClub();
        Employee employee = getEmployee();
        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(new EmployeeRole((short) 1, "ADMIN"));
        employeeRoles.add(new EmployeeRole((short) 2, "OWNER"));
        club.addEmployee(employee, employeeRoles);
        assertEquals(2, club.getEmployeesAndRoles().size());
        EmployeeRole newEmployeeRole = new EmployeeRole((short) 3, "USUAL");
        club.addRoleForEmployee(employee, newEmployeeRole);
        assertEquals(3, club.getEmployeesAndRoles().size());
        long cnt = club.getEmployeesAndRoles()
                .stream()
                .filter(employeeClub -> employeeClub.getEmployeeRole().equals(newEmployeeRole))
                .count();
        assertTrue(cnt > 0);
    }

    @Test
    @DisplayName("Удаление роли у сотрудника в клубе")
    void testDeleteRoleForEmployeeInClub() {
        Club club = getClub();
        Employee employee = getEmployee();
        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(new EmployeeRole((short) 1, "ADMIN"));
        employeeRoles.add(new EmployeeRole((short) 2, "OWNER"));
        club.addEmployee(employee, employeeRoles);
        assertEquals(2, club.getEmployeesAndRoles().size());
        boolean isDeleted = club.deleteRoleForEmployee(employee, employeeRoles.get(0));
        assertTrue(isDeleted);
        assertEquals(1, club.getEmployeesAndRoles().size());
        long cnt = club.getEmployeesAndRoles()
                .stream()
                .filter(employeeClub -> employeeClub.getEmployeeRole().equals(employeeRoles.get(0)))
                .count();
        assertEquals(0, cnt);
    }

    @Test
    @DisplayName("Удаление роли у сотрудника, которой у него не было в клубе")
    void testDeleteRoleForEmployeeInClubWhichHeDidntHave() {
        Club club = getClub();
        Employee employee = getEmployee();
        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(new EmployeeRole((short) 1, "ADMIN"));
        EmployeeRole employeeRole = new EmployeeRole((short) 2, "OWNER");
        club.addEmployee(employee, employeeRoles);
        assertEquals(1, club.getEmployeesAndRoles().size());
        boolean isDeleted = club.deleteRoleForEmployee(employee, employeeRole);
        assertFalse(isDeleted);
        assertEquals(1, club.getEmployeesAndRoles().size());
        long cnt = club.getEmployeesAndRoles()
                .stream()
                .filter(employeeClub -> employeeClub.getEmployeeRole().equals(employeeRole))
                .count();
        assertEquals(0, cnt);
    }

    @Test
    @DisplayName("Удаление сотрудника из клуба")
    void testDeleteEmployeeFromClub() {
        Club club = getClub();
        assertTrue(club.getEmployeesAndRoles().isEmpty());
        Employee employeeForDelete = getEmployee();
        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(new EmployeeRole((short) 1, "ADMIN"));
        employeeRoles.add(new EmployeeRole((short) 2, "OWNER"));
        club.addEmployee(employeeForDelete, employeeRoles);
        assertEquals(2, club.getEmployeesAndRoles().size());
        boolean isDeleted = club.deleteEmployee(employeeForDelete);
        assertTrue(isDeleted);
        assertTrue(club.getEmployeesAndRoles().isEmpty());
    }

    @Test
    @DisplayName("Удаление сотрудника из клуба, в котором он не состоит")
    void testDeleteEmployeeFromClubWhichHeWasNotIn() {
        Club club = getClub();
        assertTrue(club.getEmployeesAndRoles().isEmpty());
        Employee employee = getEmployee();
        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(new EmployeeRole((short) 1, "ADMIN"));
        employeeRoles.add(new EmployeeRole((short) 2, "OWNER"));
        club.addEmployee(employee, employeeRoles);
        assertEquals(2, club.getEmployeesAndRoles().size());
        Employee employeeForDelete = getEmployeeWithId(2);
        boolean isDeleted = club.deleteEmployee(employeeForDelete);
        assertFalse(isDeleted);
        assertEquals(2, club.getEmployeesAndRoles().size());
    }

    @Test
    @DisplayName("Добавление дня-исключения в график работы клуба")
    void testAddWorkScheduleExceptionToClub() {
        Club club = getClub();
        assertTrue(club.getWorkScheduleExceptions().isEmpty());
        WorkScheduleException workScheduleException = new WorkScheduleException();
        club.addWorkScheduleException(workScheduleException);
        assertEquals(1, club.getWorkScheduleExceptions().size());
    }

    @Test
    @DisplayName("Удаление дня-исключения из графика работы клуба")
    void testDeleteWorkScheduleExceptionToClub() {
        Club club = getClub();
        WorkScheduleException workScheduleException = new WorkScheduleException();
        club.addWorkScheduleException(workScheduleException);
        assertEquals(1, club.getWorkScheduleExceptions().size());
        boolean isDeleted = club.deleteWorkScheduleException(workScheduleException);
        assertTrue(isDeleted);
        assertTrue(club.getWorkScheduleExceptions().isEmpty());
    }

    @Test
    @DisplayName("Удаление дня-исключения из графика работы клуба которого нет в этом клубе")
    void testDeleteWorkScheduleExceptionToClubWhichHeWasNotIn() {
        Club club = getClub();
        WorkScheduleException workScheduleException = new WorkScheduleException();
        club.addWorkScheduleException(workScheduleException);
        assertEquals(1, club.getWorkScheduleExceptions().size());
        WorkScheduleException workScheduleExceptionForDelete = new WorkScheduleException(
                2,
                LocalDate.now(),
                LocalTime.now(),
                LocalTime.now().plusMinutes(10),
                true,
                null
        );
        boolean isDeleted = club.deleteWorkScheduleException(workScheduleExceptionForDelete);
        assertFalse(isDeleted);
        assertTrue(club.getWorkScheduleExceptions().contains(workScheduleException));
    }

    @Test
    @DisplayName("Изменить день из графика работы на рабочий")
    void testUpdateDayFromWorkScheduleInClubToWorkingDay() {
        Club club = getClub();
        initWorkScheduleInClubByNotWorkingDays(club);
        assertEquals(7, club.getWorkSchedules().size());
        int dayOfWeekNumber = 4;
        assertNull(club.getWorkSchedules().get(dayOfWeekNumber - 1).getOpenTime());
        assertNull(club.getWorkSchedules().get(dayOfWeekNumber - 1).getCloseTime());
        assertFalse(club.getWorkSchedules().get(dayOfWeekNumber - 1).isWorkDay());
        LocalTime openTime = LocalTime.of(10, 20);
        LocalTime closeTime = LocalTime.of(20, 10);
        club.updateDayFromWorkScheduleToWorkingDay(DayOfWeek.of(dayOfWeekNumber), openTime, closeTime);
        assertEquals(openTime, club.getWorkSchedules().get(dayOfWeekNumber - 1).getOpenTime());
        assertEquals(closeTime, club.getWorkSchedules().get(dayOfWeekNumber - 1).getCloseTime());
        assertTrue(club.getWorkSchedules().get(dayOfWeekNumber - 1).isWorkDay());
    }

    @Test
    @DisplayName("Изменить день из графика работы на нерабочий")
    void testUpdateDayFromWorkScheduleInClubToNonWorkingDay() {
        Club club = getClub();
        initWorkScheduleInClubByWorkingDays(club);
        assertEquals(7, club.getWorkSchedules().size());
        int dayOfWeekNumber = 4;
        assertNotNull(club.getWorkSchedules().get(dayOfWeekNumber - 1).getOpenTime());
        assertNotNull(club.getWorkSchedules().get(dayOfWeekNumber - 1).getCloseTime());
        assertTrue(club.getWorkSchedules().get(dayOfWeekNumber - 1).isWorkDay());
        club.updateDayFromWorkScheduleToNonWorkingDay(DayOfWeek.of(dayOfWeekNumber));
        assertNull(club.getWorkSchedules().get(dayOfWeekNumber - 1).getOpenTime());
        assertNull(club.getWorkSchedules().get(dayOfWeekNumber - 1).getCloseTime());
        assertFalse(club.getWorkSchedules().get(dayOfWeekNumber - 1).isWorkDay());
    }

    @Test
    @DisplayName("Добавление игры в клуб")
    void testAddGame() {
        Club club = getClub();
        Game game = getGame((short) 1);
        assertTrue(club.getGames().isEmpty());
        club.addGame(game);
        assertEquals(1, club.getGames().size());
        assertTrue(club.getGames().contains(game));
    }

    @Test
    @DisplayName("Удаление игры из клуба")
    void testDeleteGame() {
        Club club = getClub();
        Game game = getGame((short) 1);
        club.addGame(game);
        assertEquals(1, club.getGames().size());
        boolean isDeleted = club.deleteGame(game);
        assertTrue(isDeleted);
        assertTrue(club.getGames().isEmpty());
    }

    @Test
    @DisplayName("Удаление игры из клуба, которой нет в этом клубе")
    void testDeleteGameWhichDoesNotExitsInThisClub() {
        Club club = getClub();
        Game game = getGame((short) 1);
        club.addGame(game);
        assertEquals(1, club.getGames().size());
        Game gameForDelete = getGame((short) 2);
        boolean isDeleted = club.deleteGame(gameForDelete);
        assertFalse(isDeleted);
        assertEquals(1, club.getGames().size());
    }

    @Test
    @DisplayName("Добавление цены в клуб")
    void testAddPrice() {
        Club club = getClub();
        Price price = new Price((short) 30, new BigDecimal("100.0"));
        assertTrue(club.getPrices().isEmpty());
        club.addPrice(price);
        assertEquals(1, club.getPrices().size());
        assertTrue(club.getPrices().contains(price));
    }

    @Test
    @DisplayName("Изменение цены в клубе")
    void testUpdatePrice() {
        Club club = getClub();
        Price price = new Price((short) 30, new BigDecimal("100.0"));
        club.addPrice(price);
        boolean updated = club.updatePrice((short) 30, new BigDecimal("150.0"));
        assertTrue(updated);
        assertEquals(new BigDecimal("150.0"), club.getPrices().get(0).getValue());
    }

    @Test
    @DisplayName("Изменение цены, которой нет в клубе")
    void testUpdateNonExistingPrice() {
        Club club = getClub();
        Price price = new Price((short) 30, new BigDecimal("100.0"));
        club.addPrice(price);
        boolean updated = club.updatePrice((short) 45, new BigDecimal("200.0"));
        assertFalse(updated);
        assertEquals(1, club.getPrices().size());
    }

    @Test
    @DisplayName("Удаление цены из клуба")
    void testDeletePrice() {
        Club club = getClub();
        Price price = new Price((short) 30, new BigDecimal("100.0"));
        club.addPrice(price);
        assertEquals(1, club.getPrices().size());
        boolean isDeleted = club.deletePrice(price);
        assertTrue(isDeleted);
        assertTrue(club.getPrices().isEmpty());
    }

    @Test
    @DisplayName("Удаление цены, которой нет в клубе")
    void testDeleteNonExistingPrice() {
        Club club = getClub();
        Price price = new Price(1, (short) 30, new BigDecimal("100.0"));
        club.addPrice(price);
        Price nonExistingPrice = new Price(2, (short) 30, new BigDecimal("100.0"));
        boolean isDeleted = club.deletePrice(nonExistingPrice);
        assertFalse(isDeleted);
        assertEquals(1, club.getPrices().size());
    }

    @Test
    @DisplayName("Добавление фотографии в клуб")
    void testAddPhoto() {
        Club club = getClub();
        Photo photo = new Photo(1, "url", (short) 1);
        assertTrue(club.getPhotos().isEmpty());
        club.addPhoto(photo);
        assertEquals(1, club.getPhotos().size());
        assertTrue(club.getPhotos().contains(photo));
    }

    @Test
    @DisplayName("Переупорядочивание фотографий в клубе")
    void testReorderPhotos() {
        Club club = getClub();
        Photo photo1 = new Photo(1, "url1", (short) 1);
        Photo photo2 = new Photo(2, "url2", (short) 2);
        Photo photo3 = new Photo(3, "url3", (short) 3);
        club.addPhoto(photo1);
        club.addPhoto(photo2);
        club.addPhoto(photo3);
        assertEquals(1, photo1.getSequenceNumber());
        assertEquals(2, photo2.getSequenceNumber());
        assertEquals(3, photo3.getSequenceNumber());
        List<Long> newOrder = List.of(3L, 1L, 2L);
        club.reorderPhotos(newOrder);
        assertEquals(1, photo3.getSequenceNumber());
        assertEquals(2, photo1.getSequenceNumber());
        assertEquals(3, photo2.getSequenceNumber());
    }

    @Test
    @DisplayName("Удаление фотографии из клуба")
    void testDeletePhoto() {
        Club club = getClub();
        Photo photo = new Photo(1, "url", (short) 1);
        club.addPhoto(photo);
        assertEquals(1, club.getPhotos().size());
        boolean isDeleted = club.deletePhoto(photo);
        assertTrue(isDeleted);
        assertTrue(club.getPhotos().isEmpty());
    }

    @Test
    @DisplayName("Удаление фотографии из клуба, которой нет в этом клубе")
    void testDeletePhotoWhichDoesNotExistInClub() {
        Club club = getClub();
        Photo photo = new Photo(1, "url", (short) 1);
        club.addPhoto(photo);
        assertEquals(1, club.getPhotos().size());
        Photo photoForDelete = new Photo(2, "url", (short) 1);
        boolean isDeleted = club.deletePhoto(photoForDelete);
        assertFalse(isDeleted);
        assertEquals(1, club.getPhotos().size());
    }
}