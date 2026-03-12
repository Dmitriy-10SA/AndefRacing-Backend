package ru.andef.andefracing.backend.data.repositories.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.location.City;

@Repository
public interface CityRepository extends JpaRepository<City, Short> {
}