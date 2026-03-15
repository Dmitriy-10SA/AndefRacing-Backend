package ru.andef.andefracing.backend.data.entities.club.hr;

import lombok.Getter;

/**
 * Роль сотрудника
 */
public enum EmployeeRole {
    EMPLOYEE("Сотрудник", "EMPLOYEE"),
    ADMIN("Администратор", "ADMIN"),
    MANAGER("Управляющий", "MANAGER");

    /**
     * Представление в виде текста на русском языке
     */
    @Getter
    private final String ru;

    @Getter
    private final String role;

    EmployeeRole(String ru, String role) {
        this.ru = ru;
        this.role = role;
    }
}