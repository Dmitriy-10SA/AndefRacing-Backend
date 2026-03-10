package ru.andef.andefracing.backend.data.repositories.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andef.andefracing.backend.data.entities.location.City;

public interface CityRepository extends JpaRepository<City, Short> {
}