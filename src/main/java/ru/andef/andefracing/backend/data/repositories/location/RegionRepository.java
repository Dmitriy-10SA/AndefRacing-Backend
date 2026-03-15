package ru.andef.andefracing.backend.data.repositories.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.location.Region;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Short> {
    /**
     * Получение всех регионов с открытыми клубами
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT r.* FROM location.region r
                    JOIN location.city c on r.id = c.region_id
                    JOIN info.club cl on c.id = cl.city_id
                    WHERE cl.is_open = TRUE
                    ORDER BY r.name
                    """
    )
    List<Region> findAllRegionsWithOpenClubs();
}