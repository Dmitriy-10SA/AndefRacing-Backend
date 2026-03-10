package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
