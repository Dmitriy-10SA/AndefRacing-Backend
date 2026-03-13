package ru.andef.andefracing.backend.data.repositories.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.location.City;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Short> {
    /**
     * Получение всех городов региона с открытыми клубами
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT * FROM location.city
                    JOIN location.region r ON city.region_id = r.id
                    JOIN info.club c on city.id = c.city_id
                    WHERE c.is_open = TRUE AND r.id = :regionId
                    ORDER BY city.name
                    """
    )
    List<City> findAllCitiesInRegionWithOpenClubs(@Param(value = "regionId") short regionId);
}