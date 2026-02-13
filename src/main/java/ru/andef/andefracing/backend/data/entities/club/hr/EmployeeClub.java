package ru.andef.andefracing.backend.data.entities.club.hr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import ru.andef.andefracing.backend.data.entities.club.Club;

import java.io.Serializable;
import java.util.Objects;

/**
 * Клуб - сотрудник - роль
 *
 * @see Club клуб
 * @see Employee сотрудник
 * @see EmployeeRole роль
 */
@Entity
@Table(name = "employee_club", schema = "hr")
@IdClass(EmployeeClub.EmployeeClubId.class)
@Getter
@NoArgsConstructor
public class EmployeeClub {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_role_id", nullable = false)
    private EmployeeRole employeeRole;

    public EmployeeClub(Club club, Employee employee, EmployeeRole employeeRole) {
        this.club = club;
        this.employee = employee;
        this.employeeRole = employeeRole;
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
        return getClub() != null && Objects.equals(getClub(), that.getClub())
                && getEmployee() != null && Objects.equals(getEmployee(), that.getEmployee())
                && getEmployeeRole() != null && Objects.equals(getEmployeeRole(), that.getEmployeeRole());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(club, employee, employeeRole);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeClubId implements Serializable {
        private int club;
        private long employee;
        private short employeeRole;

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
            return Objects.equals(club, that.club)
                    && Objects.equals(employee, that.employee)
                    && Objects.equals(employeeRole, that.employeeRole);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(club, employee, employeeRole);
        }
    }
}