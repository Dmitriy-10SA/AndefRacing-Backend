package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.club.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
