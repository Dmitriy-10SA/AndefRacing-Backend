package ru.andef.andefracing.backend.data.entities.hr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import ru.andef.andefracing.backend.data.entities.info.Club;

import java.io.Serializable;
import java.util.Objects;

/**
 * Клуб - сотрудник - роль
 */
@Entity
@Table(name = "employee_club", schema = "hr")
@Getter
@NoArgsConstructor
public class EmployeeClub {
    @EmbeddedId
    private EmployeeClubId id;

    @MapsId("clubId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @MapsId("employeeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @MapsId("employeeRoleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_role_id", nullable = false)
    private EmployeeRole employeeRole;

    public EmployeeClub(Club club, Employee employee, EmployeeRole employeeRole) {
        this.club = club;
        this.employee = employee;
        this.employeeRole = employeeRole;
        this.id = new EmployeeClubId(club.getId(), employee.getId(), employeeRole.getId());
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
        EmployeeClub that = (EmployeeClub) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeClubId implements Serializable {
        @Column(name = "club_id", nullable = false)
        private int clubId;

        @Column(name = "employee_id", nullable = false)
        private long employeeId;

        @Column(name = "employee_role_id", nullable = false)
        private short employeeRoleId;

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                    ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
            Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                    ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
            if (thisEffectiveClass != oEffectiveClass) return false;
            EmployeeClubId that = (EmployeeClubId) o;
            return Objects.equals(getClubId(), that.getClubId())
                    && Objects.equals(getEmployeeId(), that.getEmployeeId())
                    && Objects.equals(getEmployeeRoleId(), that.getEmployeeRoleId());
        }

        @Override
        public final int hashCode() {
            return Objects.hash(clubId, employeeId, employeeRoleId);
        }
    }
}