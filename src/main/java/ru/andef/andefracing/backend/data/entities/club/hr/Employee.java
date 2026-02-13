package ru.andef.andefracing.backend.data.entities.club.hr;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сотрудник
 *
 * @see EmployeeClub клуб - сотрудник - роль
 * @see Booking бронирования
 */
@Entity
@Table(name = "employee", schema = "hr")
@Getter
@NoArgsConstructor
public class Employee {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "patronymic", length = 100)
    private String patronymic;

    @Column(name = "phone", unique = true, nullable = false, length = 16)
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "need_password", nullable = false)
    private boolean needPassword;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private List<EmployeeClub> clubAndRoles = new ArrayList<>();

    @OneToMany(mappedBy = "createdByEmployee", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    /**
     * Создание сотрудника с заданием пароля им самим в дальнейшем
     */
    public Employee(String surname, String name, String patronymic, String phone) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.phone = phone;
        this.password = null;
        this.needPassword = true;
        this.isBlocked = false;
    }

    /**
     * Добавление бронирования, которое было создано сотрудником, только после оплаты (с возвратом бронирования)
     */
    public Booking addBooking(
            Club club,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            short cntEquipment,
            BigDecimal priceValue
    ) {
        Booking booking = new Booking(club, startDateTime, endDateTime, cntEquipment, priceValue, this);
        bookings.add(booking);
        booking.setCreatedByEmployee(this);
        return booking;
    }

    /**
     * Отмена бронирования
     */
    public void cancelBooking(Booking booking) {
        booking.cancel();
    }

    /**
     * Установка пароля сотрудника
     */
    public void setPassword(String password) {
        if (needPassword) {
            this.needPassword = false;
        }
        this.password = password;
    }
}