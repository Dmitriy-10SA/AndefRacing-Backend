package ru.andef.andefracing.backend.data.entities.hr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.andef.andefracing.backend.data.entities.info.Club;

import java.io.Serializable;

/**
 * Клуб - сотрудник - роль
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

    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeClubId implements Serializable {
        private int club;
        private long employee;
        private short employeeRole;
    }
}