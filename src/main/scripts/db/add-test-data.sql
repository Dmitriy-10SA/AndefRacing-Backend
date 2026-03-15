--------------------------------------------------------------------------------------------
-- Добавление тестовых данных
--------------------------------------------------------------------------------------------

-- Добавление региона "Самарская область"
DO $$
DECLARE
    v_region_id SMALLINT;
    v_city_id SMALLINT;
    v_club_id INT;
BEGIN
    -- Добавляем регион
    v_region_id := admin_management.add_region('Самарская область');

    -- Добавляем город
    v_city_id := admin_management.add_city(v_region_id, 'Самара');

    -- Добавляем клуб
    v_club_id := admin_management.add_club(
        v_city_id,
        'AndefRacing ТЦ Коспопорт'::VARCHAR(100),
        '+7-999-999-99-99'::VARCHAR(16),
        'cosmoport_samara@andefracing.ru'::TEXT,
        'ТЦ Коспопорт'::TEXT,
        10::SMALLINT,
        'Семкин'::VARCHAR,
        'Дмитрий'::VARCHAR,
        'Андреевич'::VARCHAR,
        '+7-937-983-75-33'::VARCHAR
    );

    INSERT INTO info.photo(club_id, url, sequence_number)
    VALUES (v_club_id, 'https://example.com/photo1.jpg', 1);
    RAISE NOTICE 'Добавлен регион ID: %, город ID: %, клуб ID: %', v_region_id, v_city_id, v_club_id;
END $$;
