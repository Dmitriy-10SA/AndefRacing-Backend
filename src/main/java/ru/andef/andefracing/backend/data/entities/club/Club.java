package ru.andef.andefracing.backend.data.entities.club;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import ru.andef.andefracing.backend.data.entities.club.game.Game;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeClub;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.entities.club.photo.Photo;
import ru.andef.andefracing.backend.data.entities.club.price.Price;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.data.entities.location.City;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Клуб
 *
 * @see City город
 * @see Photo фотография в клубе
 * @see Price цена за кол-во минут в клубе
 * @see EmployeeClub клуб - сотрудник - роль
 * @see WorkSchedule день из графика работы клуба
 * @see WorkScheduleException день-исключение в графике работы клуба
 * @see Game игра
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "club_id", nullable = false)
    @OrderBy(value = "sequenceNumber ASC")
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EmployeeClub> employeesAndRoles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "club_id", nullable = false)
    @OrderBy(value = "durationMinutes ASC")
    private List<Price> prices = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "club_id", nullable = false)
    @OrderBy(value = "dayOfWeek ASC")
    private List<WorkSchedule> workSchedules = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "club_id", nullable = false)
    private List<WorkScheduleException> workScheduleExceptions = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "game_club",
            schema = "info",
            joinColumns = @JoinColumn(name = "club_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "game_id", nullable = false)
    )
    private List<Game> games = new ArrayList<>();

    /**
     * Добавление сотрудника в клуб
     */
    public void addEmployee(Employee employee, List<EmployeeRole> roles) {
        roles.forEach(role -> employeesAndRoles.add(new EmployeeClub(this, employee, role)));
    }

    /**
     * Добавление роли сотруднику в клубе
     */
    public void addRoleForEmployee(Employee employee, EmployeeRole role) {
        employeesAndRoles.add(new EmployeeClub(this, employee, role));
    }

    /**
     * Удаление роли у сотрудника в клубе
     */
    public boolean deleteRoleForEmployee(Employee employee, EmployeeRole role) {
        return employeesAndRoles.removeIf(employeeClub ->
                employeeClub.getEmployee().equals(employee) && employeeClub.getEmployeeRole().equals(role)
        );
    }

    /**
     * Удаление сотрудника из клуба
     */
    public boolean deleteEmployee(Employee employee) {
        return employeesAndRoles.removeIf(employeeClub -> employeeClub.getEmployee().equals(employee));
    }

    /**
     * Добавление дня-исключения в график работы клуба
     */
    public void addWorkScheduleException(WorkScheduleException workScheduleException) {
        workScheduleExceptions.add(workScheduleException);
    }

    /**
     * Удаление дня-исключения из графика работы клуба
     */
    public boolean deleteWorkScheduleException(WorkScheduleException workScheduleException) {
        return workScheduleExceptions.remove(workScheduleException);
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
    public boolean deleteGame(Game game) {
        return games.remove(game);
    }

    /**
     * Добавление цены за кол-во минут в клубе
     */
    public void addPrice(Price price) {
        prices.add(price);
    }

    /**
     * Изменение цены за кол-во минут в клубе
     */
    public boolean updatePrice(short durationMinutes, BigDecimal value) {
        for (Price price : prices) {
            if (price.getDurationMinutes() == durationMinutes) {
                price.setValue(value);
                return true;
            }
        }
        return false;
    }

    /**
     * Удаление цены за кол-во минут в клубе
     */
    public boolean deletePrice(Price price) {
        return prices.remove(price);
    }

    /**
     * Добавление фотографии в клуб
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    /**
     * Переупорядочивание фотографий в клубе
     */
    public void reorderPhotos(List<Long> orderedPhotoIds) {
        Map<Long, Photo> idAndPhoto = photos.stream().collect(Collectors.toMap(Photo::getId, it -> it));
        short sequenceNumber = 1;
        for (Long id : orderedPhotoIds) {
            Photo photo = idAndPhoto.get(id);
            photo.setSequenceNumber(sequenceNumber++);
        }
    }

    /**
     * Удаление фотографии в клубе
     */
    public boolean deletePhoto(Photo photo) {
        return photos.remove(photo);
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