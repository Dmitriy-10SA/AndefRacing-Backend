package ru.andef.andefracing.backend.data.entities.club.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import ru.andef.andefracing.backend.data.entities.client.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Бронирование (может быть сделано клиентом онлайн или сотрудником непосредственно в клубе)
 *
 * @see Club клуб
 * @see Client клиент
 * @see BookingStatus статус бронирования
 * @see Employee сотрудник
 */
@Entity
@Table(name = "booking", schema = "bookings")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @Setter
    private Client client;

    @Column(name = "start_datetime", nullable = false)
    private OffsetDateTime startDateTime;

    @Column(name = "end_datetime", nullable = false)
    private OffsetDateTime endDateTime;

    @Column(name = "cnt_equipment", nullable = false)
    private short cntEquipment;

    @Column(name = "price_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "is_walk_in", nullable = false)
    private boolean isWalkIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_employee_id")
    @Setter
    private Employee createdByEmployee;

    /**
     * Бронирование, созданное клиентом (оплата в течение 10 минут)
     */
    public Booking(
            Club club,
            Client client,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            short cntEquipment,
            BigDecimal priceValue
    ) {
        this.club = club;
        this.client = client;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.cntEquipment = cntEquipment;
        this.priceValue = priceValue;
        this.status = BookingStatus.PENDING;
        this.isWalkIn = false;
        this.createdByEmployee = null;
    }

    /**
     * Бронирование, созданное сотрудником (оплата сразу)
     */
    public Booking(
            Club club,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            short cntEquipment,
            BigDecimal priceValue,
            Employee employee
    ) {
        this.club = club;
        this.client = null;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.cntEquipment = cntEquipment;
        this.priceValue = priceValue;
        this.status = BookingStatus.PAID;
        this.isWalkIn = true;
        this.createdByEmployee = employee;
    }

    /**
     * Оплата бронирования
     */
    public void paid() {
        this.status = BookingStatus.PAID;
    }

    /**
     * Отмена бронирования
     */
    public void cancel() {
        this.status = BookingStatus.CANCELLED;
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
        Booking booking = (Booking) o;
        return Objects.equals(getId(), booking.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}