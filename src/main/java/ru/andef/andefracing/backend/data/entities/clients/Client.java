package ru.andef.andefracing.backend.data.entities.clients;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import ru.andef.andefracing.backend.data.entities.info.Club;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "client", schema = "clients")
@Getter
@NoArgsConstructor
public class Client {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false, length = 75)
    private String name;

    @Column(name = "phone", unique = true, nullable = false, length = 16)
    private String phone;

    @Column(name = "password", nullable = false)
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
    private List<Club> favoriteClubs;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Client client = (Client) o;
        return Objects.equals(getId(), client.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
