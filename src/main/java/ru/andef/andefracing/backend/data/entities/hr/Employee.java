package ru.andef.andefracing.backend.data.entities.hr;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.andef.andefracing.backend.data.entities.booking.Booking;

import java.util.List;

/**
 * Сотрудник
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
    private List<EmployeeClub> clubAndRoles;

    @OneToMany(mappedBy = "createdByEmployee", fetch = FetchType.LAZY)
    private List<Booking> bookings;

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
     * Добавление бронирования, которое было создано сотрудником (только после оплаты)
     */
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setCreatedByEmployee(this);
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