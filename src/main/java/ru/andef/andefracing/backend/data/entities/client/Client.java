package ru.andef.andefracing.backend.data.entities.client;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Клиент
 *
 * @see Club клуб
 * @see Booking бронирование
 */
@Entity
@Table(name = "client", schema = "clients")
@Getter
@NoArgsConstructor
public class Client {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false, length = 100)
    @Setter
    private String name;

    @Column(name = "phone", unique = true, nullable = false, length = 16)
    @Setter
    private String phone;

    @Column(name = "password", nullable = false)
    @Setter
    private String password;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "client_favorite_club",
            schema = "favorite",
            joinColumns = @JoinColumn(name = "client_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "club_id", nullable = false)
    )
    private List<Club> favoriteClubs = new ArrayList<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    /**
     * Регистрация клиента
     */
    public Client(String name, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.isBlocked = false;
    }

    /**
     * Сделать бронирование (с возвратом бронирования)
     */
    public Booking makeBooking(
            Club club,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            short cntEquipment,
            BigDecimal priceValue
    ) {
        Booking booking = new Booking(club, this, startDateTime, endDateTime, cntEquipment, priceValue);
        bookings.add(booking);
        booking.setClient(this);
        return booking;
    }

    /**
     * Оплатить бронирование
     */
    public void paidBooking(Booking booking) {
        booking.paid();
    }

    /**
     * Добавление клуба в список избранных
     */
    public void addFavoriteClub(Club club) {
        favoriteClubs.add(club);
    }

    /**
     * Удаление клуба из списка избранных
     */
    public boolean deleteFavoriteClub(Club club) {
        return favoriteClubs.remove(club);
    }
}