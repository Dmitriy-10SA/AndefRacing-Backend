-- триггер на создание или изменение состояния открытия клуба, если у него нет:
-- 1) основного расписания (всех 7 дней)
-- 2) хотя бы одной цены
-- 3) хотя бы одной активной игры
-- 4) хотя бы одной фотографии
CREATE OR REPLACE FUNCTION info.check_club_can_be_opened() RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.is_open = TRUE AND (TG_OP = 'INSERT' OR OLD.is_open = FALSE) THEN
        IF (SELECT COUNT(*)
            FROM info.work_schedule ws
            WHERE ws.club_id = NEW.id) != 7 THEN
            RAISE EXCEPTION 'Нельзя открыть клуб: неполное основное расписание';
        END IF;
        IF NOT EXISTS (SELECT 1
                       FROM info.price p
                       WHERE p.club_id = NEW.id) THEN
            RAISE EXCEPTION 'Нельзя открыть клуб: нет цен';
        END IF;
        IF NOT EXISTS (SELECT 1
                       FROM info.game_club gc
                                JOIN games.game g ON g.id = gc.game_id
                       WHERE gc.club_id = NEW.id
                         AND g.is_active = TRUE) THEN
            RAISE EXCEPTION 'Нельзя открыть клуб: нет активных игр';
        END IF;
        IF NOT EXISTS (SELECT 1
                       FROM info.photo ph
                       WHERE ph.club_id = NEW.id) THEN
            RAISE EXCEPTION 'Нельзя открыть клуб: нет фотографий';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_club_open
    BEFORE UPDATE OF is_open
    ON info.club
    FOR EACH ROW
EXECUTE FUNCTION info.check_club_can_be_opened();

CREATE TRIGGER trg_check_new_club_insert
    BEFORE INSERT
    ON info.club
    FOR EACH ROW
EXECUTE FUNCTION info.check_club_can_be_opened();

-- триггер на удаление последней цены в открытом клубе
CREATE OR REPLACE FUNCTION info.prevent_delete_last_price() RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM info.club c
               WHERE c.id = OLD.club_id
                 AND c.is_open = TRUE)
        AND (SELECT COUNT(*)
             FROM info.price
             WHERE club_id = OLD.club_id) = 1 THEN
        RAISE EXCEPTION 'Нельзя удалить последнюю цену у открытого клуба';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_no_delete_last_price
    BEFORE DELETE
    ON info.price
    FOR EACH ROW
EXECUTE FUNCTION info.prevent_delete_last_price();

-- триггер на удаление последней активной игры в открытом клубе
CREATE OR REPLACE FUNCTION info.prevent_delete_last_active_game() RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM info.club c
               WHERE c.id = OLD.club_id
                 AND c.is_open = TRUE)
        AND (SELECT COUNT(*)
             FROM info.game_club gc
                      JOIN games.game g ON g.id = gc.game_id
             WHERE gc.club_id = OLD.club_id
               AND g.is_active = TRUE) = 1 THEN
        RAISE EXCEPTION 'Нельзя удалить последнюю активную игру у открытого клуба';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_no_delete_last_active_game
    BEFORE DELETE
    ON info.game_club
    FOR EACH ROW
EXECUTE FUNCTION info.prevent_delete_last_active_game();

-- триггер на удаление основного графика работы (любого дня) в открытом клубе
CREATE OR REPLACE FUNCTION info.prevent_delete_work_schedule() RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM info.club c
               WHERE c.id = OLD.club_id
                 AND c.is_open = TRUE) THEN
        RAISE EXCEPTION 'Нельзя удалить день из основного графика работы у открытого клуба';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_no_delete_work_schedule
    BEFORE DELETE
    ON info.work_schedule
    FOR EACH ROW
EXECUTE FUNCTION info.prevent_delete_work_schedule();

-- триггер на удаление последней фотографии в открытом клубе
CREATE OR REPLACE FUNCTION info.prevent_delete_last_photo() RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM info.club c
               WHERE c.id = OLD.club_id
                 AND c.is_open = TRUE)
        AND (SELECT COUNT(*)
             FROM info.photo
             WHERE club_id = OLD.club_id) = 1 THEN
        RAISE EXCEPTION 'Нельзя удалить последнюю фотографию у открытого клуба';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_no_delete_last_photo
    BEFORE DELETE
    ON info.photo
    FOR EACH ROW
EXECUTE FUNCTION info.prevent_delete_last_photo();

-- триггер на попытку создать/изменить бронирование в закрытом клубе или заблокированным клиентом/сотрудником
CREATE OR REPLACE FUNCTION bookings.check_booking_entities() RETURNS TRIGGER AS
$$
BEGIN
    IF NOT EXISTS (SELECT 1
                   FROM info.club c
                   WHERE c.id = NEW.club_id
                     AND c.is_open = TRUE) THEN
        RAISE EXCEPTION 'Клуб закрыт';
    END IF;
    IF NEW.client_id IS NOT NULL AND EXISTS (SELECT 1
                                             FROM clients.client cl
                                             WHERE cl.id = NEW.client_id
                                               AND cl.is_blocked = TRUE) THEN
        RAISE EXCEPTION 'Клиент заблокирован';
    END IF;
    IF NEW.created_by_employee_id IS NOT NULL AND EXISTS (SELECT 1
                                                          FROM hr.employee e
                                                          WHERE e.id = NEW.created_by_employee_id
                                                            AND e.is_blocked = TRUE) THEN
        RAISE EXCEPTION 'Сотрудник заблокирован';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_entities_before_booking
    BEFORE INSERT OR UPDATE
    ON bookings.booking
    FOR EACH ROW
EXECUTE FUNCTION bookings.check_booking_entities();

-- триггер на бронирование в прошлом
CREATE OR REPLACE FUNCTION bookings.prevent_booking_in_past() RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.start_datetime < NOW() THEN
        RAISE EXCEPTION 'Нельзя создать бронирование в прошлом';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_booking_in_past
    BEFORE INSERT OR UPDATE
    ON bookings.booking
    FOR EACH ROW
EXECUTE FUNCTION bookings.prevent_booking_in_past();

-- триггер на бронирование, в котором кол-во оборудования больше, чем кол-во оборудования в клубе
CREATE OR REPLACE FUNCTION bookings.check_equipment_limit() RETURNS TRIGGER AS
$$
DECLARE
    club_equipment SMALLINT;
BEGIN
    SELECT cnt_equipment
    INTO club_equipment
    FROM info.club
    WHERE id = NEW.club_id;
    IF NEW.cnt_equipment > club_equipment THEN
        RAISE EXCEPTION 'Запрошено оборудования (%), но в клубе только (%)', NEW.cnt_equipment, club_equipment;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_equipment_limit
    BEFORE INSERT
    ON bookings.booking
    FOR EACH ROW
EXECUTE FUNCTION bookings.check_equipment_limit();

-- триггер на пересечение бронирований
CREATE OR REPLACE FUNCTION bookings.prevent_overlap_booking() RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM bookings.booking b
               WHERE b.club_id = NEW.club_id
                 AND b.status NOT IN ('CANCELLED', 'EXPIRED')
                 AND tstzrange(b.start_datetime, b.end_datetime) &&
                     tstzrange(NEW.start_datetime, NEW.end_datetime)) THEN
        RAISE EXCEPTION 'Пересечение с существующим бронированием в клубе';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_overlap_booking
    BEFORE INSERT OR UPDATE
    ON bookings.booking
    FOR EACH ROW
EXECUTE FUNCTION bookings.prevent_overlap_booking();

-- триггер изменения игры на неактивную, если она осталась одна в каком-либо открытом клубе
CREATE OR REPLACE FUNCTION games.prevent_deactivate_last_active_game() RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.is_active = FALSE AND OLD.is_active = TRUE THEN
        IF EXISTS (SELECT 1
                   FROM info.game_club gc
                            JOIN games.game g ON g.id = gc.game_id
                            JOIN info.club c ON c.id = gc.club_id
                   WHERE g.id = OLD.id
                     AND g.is_active = TRUE
                     AND c.is_open = TRUE
                   GROUP BY gc.club_id
                   HAVING COUNT(*) = 1) THEN
            RAISE EXCEPTION 'Нельзя деактивировать игру: она последняя активная в каком-либо открытом клубе';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_deactivate_last_active_game
    BEFORE UPDATE OF is_active
    ON games.game
    FOR EACH ROW
EXECUTE FUNCTION games.prevent_deactivate_last_active_game();