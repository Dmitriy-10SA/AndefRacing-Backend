package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andef.andefracing.backend.data.entities.club.Price;

public interface PriceRepository extends JpaRepository<Price, Long> {
}
