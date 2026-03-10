package ru.andef.andefracing.backend.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andef.andefracing.backend.data.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByPhone(String phone);
}