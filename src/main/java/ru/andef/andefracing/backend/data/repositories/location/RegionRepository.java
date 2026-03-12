package ru.andef.andefracing.backend.data.repositories.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.location.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Short> {
}