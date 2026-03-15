package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.club.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
}
