package ru.andef.andefracing.backend.data.entities.club;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeClub;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.data.entities.location.City;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Клуб
 */
@Entity
@Table(name = "club", schema = "info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "phone", nullable = false, length = 16)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "cnt_equipment", nullable = false)
    @Setter
    private short cntEquipment;

    @Column(name = "is_open", nullable = false)
    @Setter
    private boolean isOpen;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(value = "sequenceNumber ASC")
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeClub> employeesAndRoles = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "game_club",
            schema = "info",
            joinColumns = @JoinColumn(name = "club_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "game_id", nullable = false)
    )
    private List<Game> games = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(value = "durationMinutes ASC")
    private List<Price> prices = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "club", cascade = CascadeType.ALL)
    @OrderBy(value = "dayOfWeek ASC")
    private List<WorkSchedule> workSchedules = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkScheduleException> workScheduleExceptions = new ArrayList<>();

    /**
     * Добавление сотрудника в клуб
     */
    public void addEmployee(Employee employee, List<EmployeeRole> roles) {
        List<EmployeeRole> fullRoles = new ArrayList<>(roles);
        if (!fullRoles.contains(EmployeeRole.EMPLOYEE)) {
            fullRoles.add(EmployeeRole.EMPLOYEE);
        }
        fullRoles.forEach(role -> addRoleForEmployee(employee, role));
    }

    /**
     * Добавление роли сотруднику в клубе
     */
    public void addRoleForEmployee(Employee employee, EmployeeRole role) {
        EmployeeClub employeeClub = new EmployeeClub(this, employee, role);
        employeesAndRoles.add(employeeClub);
        employee.getClubAndRoles().add(employeeClub);
    }

    /**
     * Удаление роли у сотрудника в клубе
     */
    public boolean deleteRoleForEmployee(Employee employee, EmployeeRole role) {
        Iterator<EmployeeClub> iterator = employeesAndRoles.iterator();
        while (iterator.hasNext()) {
            EmployeeClub employeeClub = iterator.next();
            if (employeeClub.getEmployee().equals(employee) && employeeClub.getEmployeeRole().equals(role)) {
                iterator.remove();
                employee.getClubAndRoles().remove(employeeClub);
                return true;
            }
        }
        return false;
    }

    /**
     * Удаление сотрудника из клуба
     */
    public boolean deleteEmployee(Employee employee) {
        boolean isDeleted = false;
        Iterator<EmployeeClub> iterator = employeesAndRoles.iterator();
        while (iterator.hasNext()) {
            EmployeeClub employeeClub = iterator.next();
            if (employeeClub.getEmployee().equals(employee)) {
                iterator.remove();
                employee.getClubAndRoles().remove(employeeClub);
                isDeleted = true;
            }
        }
        return isDeleted;
    }

    /**
     * Добавление дня-исключения в график работы клуба
     */
    public void addWorkScheduleException(WorkScheduleException workScheduleException) {
        workScheduleExceptions.add(workScheduleException);
        workScheduleException.setClub(this);
    }

    /**
     * Удаление дня-исключения из графика работы клуба
     */
    public void deleteWorkScheduleException(WorkScheduleException workScheduleException) {
        workScheduleExceptions.remove(workScheduleException);
        workScheduleException.setClub(this);
    }

    /**
     * Изменить день из графика работы
     */
    private void updateDayFromWorkSchedule(
            DayOfWeek dayOfWeek,
            LocalTime openTime,
            LocalTime closeTime,
            boolean isWorkDay
    ) {
        for (WorkSchedule workSchedule : workSchedules) {
            if (workSchedule.getDayOfWeek() == dayOfWeek.getValue()) {
                workSchedule.setWorkDay(isWorkDay);
                workSchedule.setOpenTime(openTime);
                workSchedule.setCloseTime(closeTime);
                workSchedule.setClub(this);
                return;
            }
        }
    }

    /**
     * Изменить день из графика работы на рабочий
     */
    public void updateDayFromWorkScheduleToWorkingDay(
            DayOfWeek dayOfWeek,
            LocalTime openTime,
            LocalTime closeTime
    ) {
        updateDayFromWorkSchedule(dayOfWeek, openTime, closeTime, true);
    }

    /**
     * Изменить день из графика работы на нерабочий
     */
    public void updateDayFromWorkScheduleToNonWorkingDay(DayOfWeek dayOfWeek) {
        updateDayFromWorkSchedule(dayOfWeek, null, null, false);
    }

    /**
     * Добавление игры в клуб
     */
    public void addGame(Game game) {
        games.add(game);
    }

    /**
     * Удаление игры из клуба
     */
    public void deleteGame(Game game) {
        games.remove(game);
    }

    /**
     * Добавление цены за кол-во минут в клубе
     */
    public void addPrice(Price price) {
        prices.add(price);
        price.setClub(this);
    }

    /**
     * Изменение цены за кол-во минут в клубе
     */
    public boolean updatePrice(short durationMinutes, BigDecimal value) {
        for (Price price : prices) {
            if (price.getDurationMinutes() == durationMinutes) {
                price.setValue(value);
                price.setClub(this);
                return true;
            }
        }
        return false;
    }

    /**
     * Удаление цены за кол-во минут в клубе
     */
    public boolean deletePrice(Price price) {
        boolean isDeleted = prices.remove(price);
        if (isDeleted) {
            price.setClub(null);
        }
        return isDeleted;
    }

    /**
     * Добавление фотографии в клуб
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
        photo.setClub(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Club club = (Club) o;
        return Objects.equals(getId(), club.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}