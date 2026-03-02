--------------------------------------------------------------------------------------------
-- Схема для хранения всех доступных админу БД методов, а также триггеров для этих методов
--------------------------------------------------------------------------------------------
CREATE SCHEMA admin_management;

--------------------------------------------------------------------------------------------
-- Добавление региона
--------------------------------------------------------------------------------------------
CREATE
    OR REPLACE FUNCTION admin_management.add_region(p_name VARCHAR(100))
    RETURNS SMALLINT AS
$$
DECLARE
    v_id SMALLINT;
BEGIN
    INSERT INTO location.region(name)
    VALUES (p_name)
    RETURNING id
        INTO v_id;
    RETURN v_id;
END;
$$
    LANGUAGE plpgsql;

--------------------------------------------------------------------------------------------
-- Удаление региона
--------------------------------------------------------------------------------------------
CREATE
    OR REPLACE FUNCTION admin_management.delete_region(p_region_id SMALLINT)
    RETURNS VOID AS
$$
BEGIN
    DELETE
    FROM location.region
    WHERE id = p_region_id;
END;
$$
    LANGUAGE plpgsql;

--------------------------------------------------------------------------------------------
-- Редактирование региона
--------------------------------------------------------------------------------------------
CREATE
    OR REPLACE FUNCTION admin_management.update_region(p_region_id SMALLINT, p_name VARCHAR(100))
    RETURNS VOID AS
$$
BEGIN
    UPDATE location.region
    SET name = p_name
    WHERE id = p_region_id;
END;
$$
    LANGUAGE plpgsql;

--------------------------------------------------------------------------------------------
-- Добавление города
--------------------------------------------------------------------------------------------
CREATE
    OR REPLACE FUNCTION admin_management.add_city(p_region_id SMALLINT, p_name VARCHAR(100))
    RETURNS SMALLINT AS
$$
DECLARE
    v_id SMALLINT;
BEGIN
    INSERT INTO location.city(region_id, name)
    VALUES (p_region_id, p_name)
    RETURNING id
        INTO v_id;
    RETURN v_id;
END;
$$
    LANGUAGE plpgsql;

--------------------------------------------------------------------------------------------
-- Удаление города
--------------------------------------------------------------------------------------------
CREATE
    OR REPLACE FUNCTION admin_management.delete_city(p_city_id SMALLINT)
    RETURNS VOID AS
$$
BEGIN
    DELETE
    FROM location.city
    WHERE id = p_city_id;
END;
$$
    LANGUAGE plpgsql;

--------------------------------------------------------------------------------------------
-- Редактирование города
--------------------------------------------------------------------------------------------
CREATE
    OR REPLACE FUNCTION admin_management.update_city(p_city_id SMALLINT, p_name VARCHAR(100), p_region_id SMALLINT)
    RETURNS VOID AS
$$
BEGIN
    UPDATE location.city
    SET name      = p_name,
        region_id = p_region_id
    WHERE id = p_city_id;
END;
$$
    LANGUAGE plpgsql;

--------------------------------------------------------------------------------------------
-- Добавление игры в справочник
--------------------------------------------------------------------------------------------
CREATE
    OR REPLACE FUNCTION admin_management.add_game(p_name VARCHAR(100), p_photo_url TEXT,
                                                  p_is_active BOOLEAN DEFAULT TRUE)
    RETURNS SMALLINT AS
$$
DECLARE
    v_id SMALLINT;
BEGIN
    INSERT INTO games.game(name, photo_url, is_active)
    VALUES (p_name, p_photo_url, p_is_active)
    RETURNING id
        INTO v_id;
    RETURN v_id;
END;
$$
    LANGUAGE plpgsql;

--------------------------------------------------------------------------------------------
-- Удаление и редактирование игры из справочника (вместе с триггером)
--------------------------------------------------------------------------------------------
-- Проверка, что игра при удалении или изменение её статуса активности не является единственной активной в любом из открытых клубов
CREATE OR REPLACE FUNCTION admin_management.check_game_update()
    RETURNS TRIGGER AS
$$
BEGIN
    -- срабатываем только если игра реально становится неактивной
    IF OLD.is_active = TRUE AND NEW.is_active = FALSE THEN
        IF EXISTS (SELECT 1
                   FROM info.game_club gc
                            JOIN info.club c ON gc.club_id = c.id
                   WHERE gc.game_id = OLD.id
                     AND c.is_open = TRUE
                   GROUP BY gc.club_id
                   HAVING COUNT(*) = 1) THEN
            RAISE EXCEPTION 'Нельзя сделать игру неактивной: она единственная активная в каком-то открытом клубе';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Редактирование игры из справочника
CREATE
    OR REPLACE FUNCTION admin_management.update_game(
    p_game_id SMALLINT,
    p_name VARCHAR(100),
    p_photo_url TEXT,
    p_is_active BOOLEAN DEFAULT TRUE
)
    RETURNS VOID AS
$$
BEGIN
    UPDATE games.game
    SET name      = p_name,
        photo_url = p_photo_url,
        is_active = p_is_active
    WHERE id = p_game_id;
END;
$$
    LANGUAGE plpgsql;

-- Триггер на изменение статуса активности последней игры на неактивный в любом открытом клубе
CREATE TRIGGER trg_check_game_deactivate
    BEFORE UPDATE OF is_active
    ON games.game
    FOR EACH ROW
EXECUTE FUNCTION admin_management.check_game_update();

--------------------------------------------------------------------------------------------
-- Добавление клуба
--------------------------------------------------------------------------------------------
CREATE
    OR REPLACE FUNCTION admin_management.add_club(
    p_city_id SMALLINT,
    p_name VARCHAR(100),
    p_phone VARCHAR(16),
    p_email TEXT,
    p_address TEXT,
    p_cnt_equipment SMALLINT,
    p_manager_surname VARCHAR,
    p_manager_name VARCHAR,
    p_manager_patronymic VARCHAR,
    p_manager_phone VARCHAR
)
    RETURNS INT AS
$$
DECLARE
    v_club_id INT;
    v_employee_id
              BIGINT;
BEGIN
    -- Добавляем клуб как закрытый
    INSERT INTO info.club(city_id, name, phone, email, address, cnt_equipment, is_open)
    VALUES (p_city_id, p_name, p_phone, p_email, p_address, p_cnt_equipment, FALSE)
    RETURNING id
        INTO v_club_id;

-- Добавляем стандартный график работы
    INSERT INTO info.work_schedule(club_id, day_of_week, open_time, close_time, is_work_day)
    SELECT v_club_id, d, '10:00', '22:00', TRUE
    FROM generate_series(1, 7) AS d;

-- Проверяем, есть ли сотрудник с таким телефоном
    SELECT id
    INTO v_employee_id
    FROM hr.employee
    WHERE phone = p_manager_phone;

-- Если нет — создаем нового без пароля
    IF
        NOT FOUND THEN
        INSERT INTO hr.employee(surname, name, patronymic, phone, need_password, password, is_blocked)
        VALUES (p_manager_surname, p_manager_name, p_manager_patronymic, p_manager_phone, TRUE, NULL, FALSE)
        RETURNING id INTO v_employee_id;
    END IF;

-- Присваиваем роль "Управляющий"
    INSERT INTO hr.employee_club (club_id, employee_id, employee_role)
    VALUES (v_club_id, v_employee_id, 'MANAGER'::hr.employee_role);

    RETURN v_club_id;
END;
$$
    LANGUAGE plpgsql;