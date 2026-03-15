--------------------------------------------------------------------------------------------
-- Триггер для исключения случая, когда пользователь делает бронирование,
-- а оборудование уже занято (проверка в коде не подойдет, может быть race condition)
--------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION bookings.check_equipment_availability()
    RETURNS TRIGGER AS
$$
DECLARE
    used_equipment INT;
BEGIN
    SELECT COALESCE(SUM(cnt_equipment), 0)
    INTO used_equipment
    FROM bookings.booking
    WHERE club_id = NEW.club_id
      AND status != 'CANCELLED'
      AND start_datetime < NEW.end_datetime
      AND end_datetime > NEW.start_datetime;
    IF used_equipment + NEW.cnt_equipment > (SELECT cnt_equipment FROM info.club WHERE id = NEW.club_id) THEN
        RAISE EXCEPTION 'Недостаточно оборудования для бронирования';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_equipment
    BEFORE INSERT
    ON bookings.booking
    FOR EACH ROW
EXECUTE FUNCTION bookings.check_equipment_availability();