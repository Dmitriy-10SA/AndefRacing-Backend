package ru.andef.andefracing.backend.data.entities.club.hr;

import lombok.Getter;

/**
 * Роль сотрудника
 */
public enum EmployeeRole {
    EMPLOYEE("Сотрудник"),
    ADMIN("Администратор"),
    MANAGER("Управляющий");

    /**
     * Представление в виде текста на русском языке
     */
    @Getter
    private final String ru;

    EmployeeRole(String ru) {
        this.ru = ru;
    }
}