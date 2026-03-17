package ru.andef.andefracing.backend.data.entities.club;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.data.entities.location.City;

import java.math.BigDecimal;
import java.time.DayOfWeek;
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
                true,
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
        employeeRoles.add(EmployeeRole.ADMIN);
        employeeRoles.add(EmployeeRole.MANAGER);
        club.addEmployee(employee, employeeRoles);
        assertEquals(3, club.getEmployeesAndRoles().size());
        assertTrue(club.getEmployeesAndRoles().stream().allMatch(e -> e.getEmployee().equals(employee)));
    }

    @Test
    @DisplayName("Добавление роли сотруднику в клубе")
    void testAddRoleForEmployeeInClub() {
        Club club = getClub();
        Employee employee = getEmployee();
        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(EmployeeRole.ADMIN);
        employeeRoles.add(EmployeeRole.MANAGER);
        club.addEmployee(employee, employeeRoles);
        assertEquals(3, club.getEmployeesAndRoles().size());
    }

    @Test
    @DisplayName("Удаление роли у сотрудника в клубе")
    void testDeleteRoleForEmployeeInClub() {
        Club club = getClub();
        Employee employee = getEmployee();
        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(EmployeeRole.ADMIN);
        employeeRoles.add(EmployeeRole.MANAGER);
        club.addEmployee(employee, employeeRoles);
        assertEquals(3, club.getEmployeesAndRoles().size());
        boolean isDeleted = club.deleteRoleForEmployee(employee, employeeRoles.get(0));
        assertTrue(isDeleted);
        assertEquals(2, club.getEmployeesAndRoles().size());
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
        employeeRoles.add(EmployeeRole.ADMIN);
        EmployeeRole employeeRole = EmployeeRole.MANAGER;
        club.addEmployee(employee, employeeRoles);
        assertEquals(2, club.getEmployeesAndRoles().size());
        boolean isDeleted = club.deleteRoleForEmployee(employee, employeeRole);
        assertFalse(isDeleted);
        assertEquals(2, club.getEmployeesAndRoles().size());
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
        employeeRoles.add(EmployeeRole.ADMIN);
        employeeRoles.add(EmployeeRole.MANAGER);
        club.addEmployee(employeeForDelete, employeeRoles);
        assertEquals(3, club.getEmployeesAndRoles().size());
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
        employeeRoles.add(EmployeeRole.ADMIN);
        employeeRoles.add(EmployeeRole.MANAGER);
        club.addEmployee(employee, employeeRoles);
        assertEquals(3, club.getEmployeesAndRoles().size());
        Employee employeeForDelete = getEmployeeWithId(2);
        boolean isDeleted = club.deleteEmployee(employeeForDelete);
        assertFalse(isDeleted);
        assertEquals(3, club.getEmployeesAndRoles().size());
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
}