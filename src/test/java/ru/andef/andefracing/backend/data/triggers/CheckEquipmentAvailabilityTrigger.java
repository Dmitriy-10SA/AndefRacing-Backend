package ru.andef.andefracing.backend.data.triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * H2 триггер для проверки доступности оборудования при создании бронирования
 */
public class CheckEquipmentAvailabilityTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) {
        // Инициализация не требуется
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        if (newRow == null) {
            return;
        }

        // Индексы колонок в таблице bookings.booking:
        // 0: id, 1: club_id, 2: client_id, 3: start_datetime, 4: end_datetime,
        // 5: cnt_equipment, 6: price_value, 7: status, 8: is_walk_in,
        // 9: created_by_employee_id, 10: pay_confirmed_by_employee_id, 11: note

        Integer clubId = (Integer) newRow[1];
        Object startDatetime = newRow[3];
        Object endDatetime = newRow[4];
        Short cntEquipment = (Short) newRow[5];

        if (clubId == null || startDatetime == null || endDatetime == null || cntEquipment == null) {
            return;
        }

        // Получаем количество используемого оборудования в указанный период
        String usedEquipmentQuery = """
                SELECT COALESCE(SUM(cnt_equipment), 0)
                FROM bookings.booking
                WHERE club_id = ?
                AND status != 'CANCELLED'
                AND start_datetime < ?
                AND end_datetime > ?
                """;

        int usedEquipment = 0;
        try (PreparedStatement ps = conn.prepareStatement(usedEquipmentQuery)) {
            ps.setInt(1, clubId);
            ps.setObject(2, endDatetime);
            ps.setObject(3, startDatetime);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usedEquipment = rs.getInt(1);
                }
            }
        }

        // Получаем общее количество оборудования в клубе
        String totalEquipmentQuery = "SELECT cnt_equipment FROM info.club WHERE id = ?";
        short totalEquipment = 0;

        try (PreparedStatement ps = conn.prepareStatement(totalEquipmentQuery)) {
            ps.setInt(1, clubId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalEquipment = rs.getShort(1);
                }
            }
        }

        // Проверяем доступность оборудования
        if (usedEquipment + cntEquipment > totalEquipment) {
            throw new SQLException("Недостаточно оборудования для бронирования");
        }
    }

    @Override
    public void close() {
        // Очистка не требуется
    }

    @Override
    public void remove() {
        // Удаление не требуется
    }
}
