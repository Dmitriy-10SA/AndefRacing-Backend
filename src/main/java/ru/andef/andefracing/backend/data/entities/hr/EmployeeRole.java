package ru.andef.andefracing.backend.data.entities.hr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Роль сотрудника
 */
@Entity
@Table(name = "employee_role", schema = "hr")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRole {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "name", unique = true, nullable = false, length = 30)
    private String name;
}