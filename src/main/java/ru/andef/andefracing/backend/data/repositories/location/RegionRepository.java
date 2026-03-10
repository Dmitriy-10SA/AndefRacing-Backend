package ru.andef.andefracing.backend.data.repositories.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andef.andefracing.backend.data.entities.location.Region;

public interface RegionRepository extends JpaRepository<Region, Short> {
}